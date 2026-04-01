import request from '@/utils/request'
import { shouldUseMock } from '@/utils/mock'

/**
 * 上传文件到 IPFS 或本地存储
 * @param {File} file - 要上传的文件对象
 * @returns {Promise} 返回包含 fileHash、fileName、fileSize、storage、ipfsUrl/gatewayUrl 等信息
 */
export const uploadFile = (file) => {
  if (shouldUseMock()) {
    // Mock 数据
    return new Promise((resolve) => {
      setTimeout(() => {
        const mockHash = 'Qm' + Array.from({ length: 44 }, () =>
          'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789'[
            Math.floor(Math.random() * 62)
          ]
        ).join('')

        resolve({
          code: 200,
          message: '文件上传成功',
          data: {
            fileHash: mockHash,
            fileName: file.name,
            fileSize: file.size,
            contentType: file.type,
            storage: 'ipfs',
            ipfsUrl: `https://ipfs.io/ipfs/${mockHash}`,
            gatewayUrl: `https://ipfs.io/ipfs/${mockHash}`
          }
        })
      }, 1000)
    })
  }

  // 创建 FormData 对象
  const formData = new FormData()
  formData.append('file', file)

  // 调用真实后端 API
  return request({
    url: '/files/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  }).then(data => {
    // 响应拦截器已经返回 data 字段，所以这里直接处理数据
    console.log('文件上传响应:', data)

    // 处理本地文件哈希格式：local://filename?hash=<sha256>
    // 提取出真实的哈希值
    if (data && data.fileHash) {
      const fileHash = data.fileHash

      // 如果是本地文件格式，提取出 SHA256 哈希
      if (fileHash.startsWith('local://')) {
        const match = fileHash.match(/\?hash=([a-fA-F0-9]{64})/)
        if (match && match[1]) {
          // 使用真实的 SHA256 哈希替换
          data.fileHash = match[1]
          data.originalHash = fileHash // 保留原始格式供参考
          data.storage = 'local'
          data.extractedHash = true
        }
      } else if (fileHash.startsWith('Qm') || fileHash.startsWith('ba')) {
        // IPFS CID，直接使用
        data.storage = 'ipfs'
      }
    }

    return data
  })
}

/**
 * 获取文件信息
 * @param {string} fileHash - 文件哈希值
 * @returns {Promise} 返回文件信息
 */
export const getFileInfo = (fileHash) => {
  if (shouldUseMock()) {
    return Promise.resolve({
      code: 200,
      message: '获取成功',
      data: {
        fileHash: fileHash,
        storage: fileHash.startsWith('Qm') ? 'ipfs' : 'local',
        ipfsUrl: fileHash.startsWith('Qm') ? `https://ipfs.io/ipfs/${fileHash}` : null
      }
    })
  }

  return request({
    url: `/files/info/${fileHash}`,
    method: 'get'
  })
}

/**
 * 检查 IPFS 状态
 * @returns {Promise} 返回 IPFS 连接状态信息
 */
export const checkIPFSStatus = () => {
  if (shouldUseMock()) {
    return Promise.resolve({
      code: 200,
      message: 'IPFS 状态查询成功',
      data: {
        ipfsRunning: true,
        version: '0.20.0',
        nodeId: 'QmYyQSo1c1Ym7oWaoVCf7Pjn9',
        peerCount: 4
      }
    })
  }

  return request({
    url: '/files/ipfs/status',
    method: 'get'
  })
}

/**
 * 下载本地文件
 * @param {string} filename - 文件名
 * @returns {string} 下载URL
 */
export const getDownloadUrl = (filename) => {
  return `/api/files/download/${filename}`
}
