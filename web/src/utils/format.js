/**
 * 格式化数字
 * @param {number} num - 要格式化的数字
 * @returns {string} 格式化后的数字
 */
export const formatNumber = (num) => {
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + 'w'
  } else if (num >= 1000) {
    return (num / 1000).toFixed(1) + 'k'
  }
  return num.toString()
}