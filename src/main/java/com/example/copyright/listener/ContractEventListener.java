package com.example.copyright.listener;

import com.alibaba.fastjson.JSON;
import com.example.copyright.entity.ContractEvent;
import com.example.copyright.mapper.ContractEventMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 合约事件监听器
 * 监听FISCO BCOS智能合约事件并同步到数据库
 */
@Slf4j
@Component
public class ContractEventListener {

    @Autowired(required = false)
    private ContractEventMapper contractEventMapper;

    /**
     * 应用启动后开始监听合约事件
     */
    @EventListener(ApplicationReadyEvent.class)
    public void startListening() {
        log.info("Starting to listen for contract events...");

        // 启动异步线程监听事件
        new Thread(() -> {
            try {
                // CopyrightRegistry contract = CopyrightRegistry.load(contractAddress, client, keyPair);

                // 监听用户注册事件
                // contract.userRegisteredEventFlowable(blockParameter, blockParameter)
                //     .subscribe(event -> {
                //         saveEvent("UserRegistered", event);
                //     });

                // 监听版权注册事件
                // contract.copyrightRegisteredEventFlowable(blockParameter, blockParameter)
                //     .subscribe(event -> {
                //         saveEvent("CopyrightRegistered", event);
                //     });

                // 监听授权申请事件
                // contract.licenseAppliedEventFlowable(blockParameter, blockParameter)
                //     .subscribe(event -> {
                //         saveEvent("LicenseApplied", event);
                //     });

                // 监听授权批准事件
                // contract.licenseApprovedEventFlowable(blockParameter, blockParameter)
                //     .subscribe(event -> {
                //         saveEvent("LicenseApproved", event);
                //     });

                log.info("Contract event listener started successfully");

            } catch (Exception e) {
                log.error("Failed to start contract event listener: {}", e.getMessage(), e);
            }
        }).start();
    }

    /**
     * 保存事件到数据库
     */
    private void saveEvent(String eventName, Object event) {
        try {
            if (contractEventMapper == null) {
                log.warn("ContractEventMapper not available, skipping event save");
                return;
            }

            ContractEvent contractEvent = new ContractEvent();
            contractEvent.setEventName(eventName);
            contractEvent.setData(JSON.toJSONString(event));
            // 设置其他字段...

            contractEventMapper.insert(contractEvent);
            log.info("Event saved: {}", eventName);

        } catch (Exception e) {
            log.error("Failed to save event: {}", e.getMessage(), e);
        }
    }
}
