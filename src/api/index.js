import axios from 'axios'

export const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'https://azhar-fund.onrender.com',
  headers: {
    // 'Content-Type': 'application/json',
    Accept: 'application/json',
  },
})
