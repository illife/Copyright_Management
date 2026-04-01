package com.example.copyright.service;

import com.example.copyright.entity.BlockInfo;
import com.example.copyright.entity.TransactionInfo;
import com.example.copyright.mapper.BlockInfoMapper;
import com.example.copyright.mapper.TransactionInfoMapper;
import com.example.copyright.vo.BlockVO;
import com.example.copyright.vo.ChainStatsVO;
import com.example.copyright.vo.TransactionVO;
import lombok.extern.slf4j.Slf4j;
import org.fisco.bcos.sdk.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 区块链浏览器服务
 * 提供区块链数据查询功能
 */
@Slf4j
@Service
public class BlockchainExplorerService {

    @Autowired
    private Client client;

    @Autowired(required = false)
    private BlockInfoMapper blockInfoMapper;

    @Autowired(required = false)
    private TransactionInfoMapper transactionInfoMapper;

    /**
     * 获取链统计信息
     */
    public ChainStatsVO getChainStats() {
        try {
            ChainStatsVO stats = new ChainStatsVO();

            // 获取区块高度
            BigInteger blockNumber = client.getBlockNumber().getBlockNumber();
            stats.setLatestBlockNumber(blockNumber);

            // 获取群组ID
            stats.setGroupId(client.getGroupId());

            // 获取系统版本
            stats.setSystemVersion("FISCO BCOS 2.x");

            // 获取PBFT视图
            try {
                Object pbftViewObj = client.getPbftView();
                if (pbftViewObj != null) {
                    long pbftView;
                    if (pbftViewObj instanceof BigInteger) {
                        pbftView = ((BigInteger) pbftViewObj).longValue();
                    } else if (pbftViewObj instanceof String) {
                        pbftView = Long.parseLong((String) pbftViewObj);
                    } else {
                        pbftView = Long.parseLong(pbftViewObj.toString());
                    }
                    stats.setPbftView(pbftView);
                }
            } catch (Exception e) {
                log.warn("Failed to get PBFT view: {}", e.getMessage());
            }

            // 获取节点数量（设置为默认值）
            stats.setNodeCount(1);

            // 从数据库获取总交易数（如果有的话）
            if (transactionInfoMapper != null) {
                try {
                    Long totalTx = transactionInfoMapper.countTotalTransactions();
                    stats.setTotalTransactions(totalTx != null ? BigInteger.valueOf(totalTx) : BigInteger.ZERO);
                } catch (Exception e) {
                    log.warn("Failed to get total transactions from database: {}", e.getMessage());
                    stats.setTotalTransactions(BigInteger.ZERO);
                }
            } else {
                stats.setTotalTransactions(BigInteger.ZERO);
            }

            // 获取最新区块哈希
            if (stats.getLatestBlockNumber() != null) {
                try {
                    BlockVO latestBlock = getBlockByNumber(stats.getLatestBlockNumber());
                    if (latestBlock != null) {
                        stats.setLatestBlockHash(latestBlock.getBlockHash());
                    }
                } catch (Exception e) {
                    log.warn("Failed to get latest block hash: {}", e.getMessage());
                }
            }

            stats.setSyncing(false);

            return stats;

        } catch (Exception e) {
            log.error("Failed to get chain stats: {}", e.getMessage(), e);
            throw new RuntimeException("获取链统计信息失败: " + e.getMessage(), e);
        }
    }

    /**
     * 根据区块号获取区块信息
     */
    public BlockVO getBlockByNumber(BigInteger blockNumber) {
        try {
            // 先从数据库查询
            if (blockInfoMapper != null) {
                try {
                    BlockInfo cachedBlock = blockInfoMapper.selectByBlockNumber(blockNumber);
                    if (cachedBlock != null) {
                        return convertToBlockVO(cachedBlock);
                    }
                } catch (Exception e) {
                    log.warn("Failed to query block from database: {}", e.getMessage());
                }
            }

            // 从链上获取 - 使用简化的方法
            try {
                org.fisco.bcos.sdk.client.protocol.response.BcosBlock bcosBlock =
                    client.getBlockByNumber(blockNumber, false);
                if (bcosBlock != null && bcosBlock.getBlock() != null) {
                    org.fisco.bcos.sdk.client.protocol.response.BcosBlock.Block block = bcosBlock.getBlock();
                    BlockVO blockVO = parseBlockData(block);

                    // 保存到数据库
                    if (blockVO != null && blockInfoMapper != null) {
                        try {
                            saveBlockToDatabase(blockVO);
                        } catch (Exception e) {
                            log.warn("Failed to save block to database: {}", e.getMessage());
                        }
                    }

                    return blockVO;
                }
            } catch (Exception e) {
                log.error("Failed to get block from chain: {}", e.getMessage());
            }

            return null;

        } catch (Exception e) {
            log.error("Failed to get block by number: {}", e.getMessage(), e);
            throw new RuntimeException("获取区块信息失败: " + e.getMessage(), e);
        }
    }

    /**
     * 根据区块哈希获取区块信息
     */
    public BlockVO getBlockByHash(String blockHash) {
        try {
            // 先从数据库查询
            if (blockInfoMapper != null) {
                try {
                    BlockInfo cachedBlock = blockInfoMapper.selectByBlockHash(blockHash);
                    if (cachedBlock != null) {
                        return convertToBlockVO(cachedBlock);
                    }
                } catch (Exception e) {
                    log.warn("Failed to query block from database: {}", e.getMessage());
                }
            }

            // 从链上获取
            try {
                org.fisco.bcos.sdk.client.protocol.response.BcosBlock bcosBlock =
                    client.getBlockByHash(blockHash, false);
                if (bcosBlock != null && bcosBlock.getBlock() != null) {
                    org.fisco.bcos.sdk.client.protocol.response.BcosBlock.Block block = bcosBlock.getBlock();
                    BlockVO blockVO = parseBlockData(block);

                    // 保存到数据库
                    if (blockVO != null && blockInfoMapper != null) {
                        try {
                            saveBlockToDatabase(blockVO);
                        } catch (Exception e) {
                            log.warn("Failed to save block to database: {}", e.getMessage());
                        }
                    }

                    return blockVO;
                }
            } catch (Exception e) {
                log.error("Failed to get block from chain: {}", e.getMessage());
            }

            return null;

        } catch (Exception e) {
            log.error("Failed to get block by hash: {}", e.getMessage(), e);
            throw new RuntimeException("获取区块信息失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取最新的区块列表
     */
    public List<BlockVO> getLatestBlocks(int limit) {
        try {
            BigInteger blockNumber = client.getBlockNumber().getBlockNumber();
            List<BlockVO> blocks = new ArrayList<>();

            int count = 0;
            for (BigInteger i = blockNumber; i.compareTo(BigInteger.ZERO) > 0 && count < limit; i = i.subtract(BigInteger.ONE)) {
                try {
                    BlockVO block = getBlockByNumber(i);
                    if (block != null) {
                        blocks.add(block);
                        count++;
                    }
                } catch (Exception e) {
                    log.warn("Failed to get block {}: {}", i, e.getMessage());
                }
            }

            return blocks;

        } catch (Exception e) {
            log.error("Failed to get latest blocks: {}", e.getMessage(), e);
            throw new RuntimeException("获取最新区块列表失败: " + e.getMessage(), e);
        }
    }

    /**
     * 根据交易哈希获取交易信息
     */
    public TransactionVO getTransactionByHash(String transactionHash) {
        try {
            // 先从数据库查询
            if (transactionInfoMapper != null) {
                try {
                    TransactionInfo cachedTx = transactionInfoMapper.selectByTransactionHash(transactionHash);
                    if (cachedTx != null) {
                        return convertToTransactionVO(cachedTx);
                    }
                } catch (Exception e) {
                    log.warn("Failed to query transaction from database: {}", e.getMessage());
                }
            }

            // 从链上获取 - 使用收据信息
            try {
                log.info("Getting transaction by hash: {}", transactionHash);

                // 获取交易收据
                org.fisco.bcos.sdk.model.TransactionReceipt receipt = null;
                try {
                    receipt = client.getTransactionReceipt(transactionHash).getTransactionReceipt().orElse(null);
                } catch (Exception e) {
                    log.warn("Failed to get transaction receipt: {}", e.getMessage());
                }

                if (receipt != null) {
                    TransactionVO txVO = parseTransactionReceipt(receipt, transactionHash);

                    // 保存到数据库
                    if (txVO != null && transactionInfoMapper != null) {
                        try {
                            saveTransactionToDatabase(txVO);
                        } catch (Exception e) {
                            log.warn("Failed to save transaction to database: {}", e.getMessage());
                        }
                    }

                    return txVO;
                }
            } catch (Exception e) {
                log.error("Failed to get transaction from chain: {}", e.getMessage(), e);
            }

            return null;

        } catch (Exception e) {
            log.error("Failed to get transaction by hash: {}", e.getMessage(), e);
            throw new RuntimeException("获取交易信息失败: " + e.getMessage(), e);
        }
    }

    /**
     * 从交易收据解析交易数据
     */
    private TransactionVO parseTransactionReceipt(
            org.fisco.bcos.sdk.model.TransactionReceipt receipt,
            String transactionHash) {
        try {
            TransactionVO txVO = new TransactionVO();

            txVO.setTransactionHash(transactionHash);

            try {
                String blockNumberStr = receipt.getBlockNumber();
                if (blockNumberStr != null && !blockNumberStr.isEmpty()) {
                    txVO.setBlockNumber(new BigInteger(blockNumberStr));
                }
            } catch (Exception e) {
                log.warn("Failed to get block number: {}", e.getMessage());
            }

            try {
                String blockHash = receipt.getBlockHash();
                if (blockHash != null) {
                    txVO.setBlockHash(blockHash);
                }
            } catch (Exception e) {
                log.warn("Failed to get block hash: {}", e.getMessage());
            }

            try {
                String from = receipt.getFrom();
                if (from != null) {
                    txVO.setFrom(from);
                }
            } catch (Exception e) {
                log.warn("Failed to get from: {}", e.getMessage());
            }

            try {
                String to = receipt.getTo();
                if (to != null) {
                    txVO.setTo(to);
                }
            } catch (Exception e) {
                log.warn("Failed to get to: {}", e.getMessage());
            }

            try {
                String gasUsedStr = receipt.getGasUsed();
                if (gasUsedStr != null && !gasUsedStr.isEmpty()) {
                    // 去掉0x前缀
                    if (gasUsedStr.startsWith("0x")) {
                        txVO.setGasUsed(new BigInteger(gasUsedStr.substring(2), 16));
                    } else {
                        txVO.setGasUsed(new BigInteger(gasUsedStr));
                    }
                }
            } catch (Exception e) {
                log.warn("Failed to get gas used: {}", e.getMessage());
            }

            try {
                String statusStr = receipt.getStatus();
                if (statusStr != null && !statusStr.isEmpty()) {
                    int status;
                    if (statusStr.startsWith("0x")) {
                        status = Integer.parseInt(statusStr.substring(2), 16);
                    } else {
                        status = Integer.parseInt(statusStr);
                    }
                    // FISCO BCOS: 0表示成功
                    txVO.setStatus(status == 0 ? 1 : 0);
                }
            } catch (Exception e) {
                log.warn("Failed to get status: {}", e.getMessage());
                txVO.setStatus(0);
            }

            // 尝试获取输入数据
            try {
                String input = receipt.getInput();
                if (input != null && !input.isEmpty()) {
                    txVO.setInputData(input);
                    // 解析方法名
                    if (input.length() > 10) {
                        String methodSig = input.substring(0, 10);
                        txVO.setMethodName(methodSig);
                    }
                }
            } catch (Exception e) {
                log.warn("Failed to get input: {}", e.getMessage());
            }

            // 交易索引
            txVO.setTransactionIndex(0);

            // 时间戳（从区块获取）
            try {
                if (txVO.getBlockNumber() != null) {
                    org.fisco.bcos.sdk.client.protocol.response.BcosBlock.Block block =
                        client.getBlockByNumber(txVO.getBlockNumber(), false).getBlock();
                    if (block != null && block.getTimestamp() != null) {
                        Object timestampObj = block.getTimestamp();
                        if (timestampObj != null) {
                            long timestamp;
                            if (timestampObj instanceof String) {
                                timestamp = new BigInteger((String) timestampObj).longValue();
                            } else if (timestampObj instanceof BigInteger) {
                                timestamp = ((BigInteger) timestampObj).longValue();
                            } else {
                                timestamp = Long.parseLong(timestampObj.toString());
                            }
                            txVO.setTimestamp(LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneId.systemDefault()));
                        }
                    }
                }
            } catch (Exception e) {
                log.warn("Failed to get block timestamp: {}", e.getMessage());
            }

            return txVO;

        } catch (Exception e) {
            log.error("Failed to parse transaction receipt: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 根据区块号获取交易列表
     */
    public List<TransactionVO> getTransactionsByBlock(BigInteger blockNumber) {
        try {
            BlockVO block = getBlockByNumber(blockNumber);
            if (block == null) {
                return Collections.emptyList();
            }

            // 先从数据库查询
            if (transactionInfoMapper != null) {
                try {
                    List<TransactionInfo> cachedTxs = transactionInfoMapper.selectByBlockNumber(blockNumber);
                    if (!cachedTxs.isEmpty()) {
                        return cachedTxs.stream()
                                .map(this::convertToTransactionVO)
                                .collect(Collectors.toList());
                    }
                } catch (Exception e) {
                    log.warn("Failed to query transactions from database: {}", e.getMessage());
                }
            }

            // 从链上获取
            List<TransactionVO> transactions = new ArrayList<>();
            try {
                org.fisco.bcos.sdk.client.protocol.response.BcosBlock bcosBlock =
                    client.getBlockByNumber(blockNumber, true);
                if (bcosBlock != null && bcosBlock.getBlock() != null) {
                    List<?> txs = bcosBlock.getBlock().getTransactions();
                    if (txs != null) {
                        for (Object txObj : txs) {
                            try {
                                // 获取交易哈希 - 使用反射
                                String txHash = null;
                                try {
                                    java.lang.reflect.Field hashField = txObj.getClass().getDeclaredField("hash");
                                    hashField.setAccessible(true);
                                    txHash = (String) hashField.get(txObj);
                                } catch (Exception e) {
                                    log.warn("Failed to get tx hash via reflection: {}", e.getMessage());
                                }

                                if (txHash != null) {
                                    // 获取交易详细信息
                                    org.fisco.bcos.sdk.model.TransactionReceipt receipt = null;
                                    try {
                                        receipt = client.getTransactionReceipt(txHash).getTransactionReceipt().orElse(null);
                                    } catch (Exception e) {
                                        log.warn("Failed to get receipt: {}", e.getMessage());
                                    }

                                    TransactionVO txVO = parseTransactionReceipt(receipt, txHash);
                                    if (txVO != null) {
                                        transactions.add(txVO);
                                        if (transactionInfoMapper != null) {
                                            try {
                                                saveTransactionToDatabase(txVO);
                                            } catch (Exception e1) {
                                                log.warn("Failed to save transaction: {}", e1.getMessage());
                                            }
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                log.warn("Failed to parse transaction: {}", e.getMessage());
                            }
                        }
                    }
                }
            } catch (Exception e) {
                log.error("Failed to get transactions from block: {}", e.getMessage());
            }

            return transactions;

        } catch (Exception e) {
            log.error("Failed to get transactions by block: {}", e.getMessage(), e);
            throw new RuntimeException("获取区块交易失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取最新的交易列表
     */
    public List<TransactionVO> getLatestTransactions(int limit) {
        try {
            // 从数据库获取最新的交易
            if (transactionInfoMapper != null) {
                try {
                    List<TransactionInfo> cachedTxs = transactionInfoMapper.selectLatestTransactions(limit);
                    if (!cachedTxs.isEmpty()) {
                        return cachedTxs.stream()
                                .map(this::convertToTransactionVO)
                                .collect(Collectors.toList());
                    }
                } catch (Exception e) {
                    log.warn("Failed to query latest transactions from database: {}", e.getMessage());
                }
            }

            // 从最新的几个区块中获取交易
            List<TransactionVO> allTransactions = new ArrayList<>();
            BigInteger blockNumber = client.getBlockNumber().getBlockNumber();

            int blockCount = 0;
            for (BigInteger i = blockNumber; i.compareTo(BigInteger.ZERO) > 0 && allTransactions.size() < limit; i = i.subtract(BigInteger.ONE)) {
                try {
                    List<TransactionVO> blockTxs = getTransactionsByBlock(i);
                    allTransactions.addAll(blockTxs);
                    blockCount++;
                    if (blockCount >= 10) break; // 最多查询10个区块
                } catch (Exception e) {
                    log.warn("Failed to get transactions from block {}: {}", i, e.getMessage());
                }
            }

            return allTransactions.stream()
                    .limit(limit)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Failed to get latest transactions: {}", e.getMessage(), e);
            throw new RuntimeException("获取最新交易列表失败: " + e.getMessage(), e);
        }
    }

    /**
     * 根据地址查询交易（作为发送方或接收方）
     */
    public List<TransactionVO> getTransactionsByAddress(String address, int limit) {
        try {
            List<TransactionVO> allTransactions = new ArrayList<>();

            if (transactionInfoMapper != null) {
                // 查询作为发送方的交易
                try {
                    List<TransactionInfo> fromTxs = transactionInfoMapper.selectByFromAddress(address, limit);
                    allTransactions.addAll(fromTxs.stream()
                            .map(this::convertToTransactionVO)
                            .collect(Collectors.toList()));
                } catch (Exception e) {
                    log.warn("Failed to query transactions by from address: {}", e.getMessage());
                }

                // 查询作为接收方的交易
                try {
                    List<TransactionInfo> toTxs = transactionInfoMapper.selectByToAddress(address, limit);
                    allTransactions.addAll(toTxs.stream()
                            .map(this::convertToTransactionVO)
                            .collect(Collectors.toList()));
                } catch (Exception e) {
                    log.warn("Failed to query transactions by to address: {}", e.getMessage());
                }

                // 去重并限制数量
                return allTransactions.stream()
                        .collect(Collectors.toMap(
                                TransactionVO::getTransactionHash,
                                tx -> tx,
                                (existing, replacement) -> existing
                        ))
                        .values()
                        .stream()
                        .sorted((a, b) -> b.getBlockNumber().compareTo(a.getBlockNumber()))
                        .limit(limit)
                        .collect(Collectors.toList());
            }

            return Collections.emptyList();

        } catch (Exception e) {
            log.error("Failed to get transactions by address: {}", e.getMessage(), e);
            throw new RuntimeException("根据地址查询交易失败: " + e.getMessage(), e);
        }
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 解析区块数据 - 使用try-catch来处理不同的字段类型
     */
    private BlockVO parseBlockData(org.fisco.bcos.sdk.client.protocol.response.BcosBlock.Block block) {
        try {
            BlockVO blockVO = new BlockVO();

            try {
                blockVO.setBlockNumber(block.getNumber());
            } catch (Exception e) {
                log.warn("Failed to get block number: {}", e.getMessage());
            }

            try {
                blockVO.setBlockHash(block.getHash());
            } catch (Exception e) {
                log.warn("Failed to get block hash: {}", e.getMessage());
            }

            try {
                blockVO.setParentHash(block.getParentHash());
            } catch (Exception e) {
                log.warn("Failed to get parent hash: {}", e.getMessage());
            }

            // 时间戳转换
            try {
                Object timestampObj = block.getTimestamp();
                if (timestampObj != null) {
                    long timestamp;
                    if (timestampObj instanceof String) {
                        timestamp = new BigInteger((String) timestampObj).longValue();
                    } else if (timestampObj instanceof BigInteger) {
                        timestamp = ((BigInteger) timestampObj).longValue();
                    } else {
                        timestamp = Long.parseLong(timestampObj.toString());
                    }
                    blockVO.setTimestamp(LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneId.systemDefault()));
                }
            } catch (Exception e) {
                log.warn("Failed to get timestamp: {}", e.getMessage());
            }

            // 交易数量
            try {
                List<?> transactions = block.getTransactions();
                blockVO.setTransactionCount(transactions != null ? transactions.size() : 0);
            } catch (Exception e) {
                log.warn("Failed to get transactions: {}", e.getMessage());
                blockVO.setTransactionCount(0);
            }

            // gas使用量
            try {
                Object gasUsedObj = block.getGasUsed();
                if (gasUsedObj != null) {
                    if (gasUsedObj instanceof BigInteger) {
                        blockVO.setGasUsed((BigInteger) gasUsedObj);
                    } else {
                        blockVO.setGasUsed(new BigInteger(gasUsedObj.toString()));
                    }
                }
            } catch (Exception e) {
                log.warn("Failed to get gas used: {}", e.getMessage());
            }

            // gas限制
            try {
                Object gasLimitObj = block.getGasLimit();
                if (gasLimitObj != null) {
                    if (gasLimitObj instanceof BigInteger) {
                        blockVO.setGasLimit((BigInteger) gasLimitObj);
                    } else {
                        blockVO.setGasLimit(new BigInteger(gasLimitObj.toString()));
                    }
                }
            } catch (Exception e) {
                log.warn("Failed to get gas limit: {}", e.getMessage());
            }

            // 区块大小（估算值：使用hash长度的一半作为字节数）
            blockVO.setBlockSize(BigInteger.valueOf(block.getHash().length() / 2));

            // 区块生成者（sealer）
            try {
                Object sealerObj = block.getSealer();
                if (sealerObj != null) {
                    long sealer;
                    if (sealerObj instanceof BigInteger) {
                        sealer = ((BigInteger) sealerObj).longValue();
                    } else {
                        sealer = Long.parseLong(sealerObj.toString());
                    }
                    blockVO.setSealer(String.format("0x%x", sealer));
                }
            } catch (Exception e) {
                log.warn("Failed to get sealer: {}", e.getMessage());
            }

            blockVO.setStatus("正常");

            return blockVO;

        } catch (Exception e) {
            log.error("Failed to parse block data: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 保存区块到数据库
     */
    private void saveBlockToDatabase(BlockVO blockVO) {
        try {
            BlockInfo blockInfo = new BlockInfo();
            blockInfo.setBlockNumber(blockVO.getBlockNumber());
            blockInfo.setBlockHash(blockVO.getBlockHash());
            blockInfo.setParentHash(blockVO.getParentHash());
            blockInfo.setTimestamp(blockVO.getTimestamp());
            blockInfo.setTransactionCount(blockVO.getTransactionCount());
            blockInfo.setSealer(blockVO.getSealer());
            blockInfo.setGasUsed(blockVO.getGasUsed());
            blockInfo.setGasLimit(blockVO.getGasLimit());
            blockInfo.setBlockSize(blockVO.getBlockSize());
            blockInfo.setCreatedAt(LocalDateTime.now());
            blockInfo.setUpdatedAt(LocalDateTime.now());

            // 检查是否已存在
            BlockInfo existing = blockInfoMapper.selectByBlockNumber(blockVO.getBlockNumber());
            if (existing == null) {
                blockInfoMapper.insert(blockInfo);
            }

        } catch (Exception e) {
            log.warn("Failed to save block to database: {}", e.getMessage());
        }
    }

    /**
     * 保存交易到数据库
     */
    private void saveTransactionToDatabase(TransactionVO txVO) {
        try {
            TransactionInfo txInfo = new TransactionInfo();
            txInfo.setTransactionHash(txVO.getTransactionHash());
            txInfo.setBlockNumber(txVO.getBlockNumber());
            txInfo.setBlockHash(txVO.getBlockHash());
            txInfo.setTransactionIndex(txVO.getTransactionIndex());
            txInfo.setFrom(txVO.getFrom());
            txInfo.setTo(txVO.getTo());
            txInfo.setValue(txVO.getValue());
            txInfo.setGasPrice(txVO.getGasPrice());
            txInfo.setGasUsed(txVO.getGasUsed());
            txInfo.setInputData(txVO.getInputData());
            txInfo.setStatus(txVO.getStatus());
            txInfo.setMethodName(txVO.getMethodName());
            txInfo.setTimestamp(txVO.getTimestamp());
            txInfo.setCreatedAt(LocalDateTime.now());
            txInfo.setUpdatedAt(LocalDateTime.now());

            // 检查是否已存在
            TransactionInfo existing = transactionInfoMapper.selectByTransactionHash(txVO.getTransactionHash());
            if (existing == null) {
                transactionInfoMapper.insert(txInfo);
            }

        } catch (Exception e) {
            log.warn("Failed to save transaction to database: {}", e.getMessage());
        }
    }

    /**
     * 转换为BlockVO
     */
    private BlockVO convertToBlockVO(BlockInfo blockInfo) {
        BlockVO blockVO = new BlockVO();
        blockVO.setBlockNumber(blockInfo.getBlockNumber());
        blockVO.setBlockHash(blockInfo.getBlockHash());
        blockVO.setParentHash(blockInfo.getParentHash());
        blockVO.setTimestamp(blockInfo.getTimestamp());
        blockVO.setTransactionCount(blockInfo.getTransactionCount());
        blockVO.setSealer(blockInfo.getSealer());
        blockVO.setGasUsed(blockInfo.getGasUsed());
        blockVO.setGasLimit(blockInfo.getGasLimit());
        blockVO.setBlockSize(blockInfo.getBlockSize());
        blockVO.setStatus("正常");
        return blockVO;
    }

    /**
     * 转换为TransactionVO
     */
    private TransactionVO convertToTransactionVO(TransactionInfo txInfo) {
        TransactionVO txVO = new TransactionVO();
        txVO.setTransactionHash(txInfo.getTransactionHash());
        txVO.setBlockNumber(txInfo.getBlockNumber());
        txVO.setBlockHash(txInfo.getBlockHash());
        txVO.setTransactionIndex(txInfo.getTransactionIndex());
        txVO.setFrom(txInfo.getFrom());
        txVO.setTo(txInfo.getTo());
        txVO.setValue(txInfo.getValue());
        txVO.setGasPrice(txInfo.getGasPrice());
        txVO.setGasUsed(txInfo.getGasUsed());
        txVO.setInputData(txInfo.getInputData());
        txVO.setStatus(txInfo.getStatus());
        txVO.setMethodName(txInfo.getMethodName());
        txVO.setTimestamp(txInfo.getTimestamp());
        return txVO;
    }
}
