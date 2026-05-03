/**
 * Базовый URL бэкенда
 */
export function getBackendUrl() {
  return 'https://azhar-fund.onrender.com'
}

/**
 * Получить полный URL для файлов
 */
export function getFileUrl(path) {
  if (!path) return ''

  // если уже полный URL
  if (path.startsWith('http://') || path.startsWith('https://')) {
    return path
  }

  const backendUrl = getBackendUrl()

  let cleanPath = path

  // убираем /app если есть (Render)
  if (cleanPath.startsWith('/app/')) {
    cleanPath = cleanPath.substring(5)
  } else if (cleanPath.startsWith('app/')) {
    cleanPath = cleanPath.substring(4)
  }

  if (cleanPath.startsWith('/')) {
    return `${backendUrl}${cleanPath}`
  }

  if (
    cleanPath.startsWith('images/') ||
    cleanPath.startsWith('docs/') ||
    cleanPath.startsWith('avatars/')
  ) {
    return `${backendUrl}/${cleanPath}`
  }

  return `${backendUrl}/${cleanPath}`
}
