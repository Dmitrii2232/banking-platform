'use client'

import { useState } from 'react'
import { useRouter, useSearchParams } from 'next/navigation'
import { motion } from 'framer-motion'
import { Lock, Phone, Eye, EyeOff, Loader2 } from 'lucide-react'
import Link from 'next/link'
import { authService } from '@/lib/auth'

export default function LoginPage() {
  const router = useRouter()
  const searchParams = useSearchParams()
  const [phone, setPhone] = useState(searchParams.get('phone') || '')
  const [password, setPassword] = useState('')
  const [showPassword, setShowPassword] = useState(false)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  
  const registered = searchParams.get('registered')
  const redirect = searchParams.get('redirect')

  const formatPhone = (value: string): string => {
    const digits = value.replace(/\D/g, '')
    if (digits.startsWith('7') || digits.startsWith('8')) {
      const match = digits.match(/^[78]?(\d{0,3})(\d{0,3})(\d{0,2})(\d{0,2})$/)
      if (match) {
        return `+7 (${match[1] ? match[1] : ''}${match[2] ? ') ' + match[2] : ''}${match[3] ? '-' + match[3] : ''}${match[4] ? '-' + match[4] : ''}`
      }
    }
    return `+7 ${digits}`
  }

  const handlePhoneChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setPhone(formatPhone(e.target.value))
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError('')
    setLoading(true)

    const cleanPhone = phone.replace(/[\s\(\)-]/g, '')

    try {
      const result = await authService.login({ phone: cleanPhone, password })
      
      if (result.success) {
        const redirectPath = redirect || '/dashboard'
        router.push(redirectPath)
        router.refresh()
      } else {
        setError(result.message || 'Ошибка входа')
      }
    } catch (err: any) {
      console.error('Login error:', err)
      setError(err.message || 'Ошибка при входе')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-blue-50 to-cyan-50 py-12 px-4">
      {/* Кнопка назад */}
      <Link 
        href="/" 
        className="absolute top-6 left-6 inline-flex items-center text-gray-500 hover:text-gray-700 bg-white/80 hover:bg-white rounded-lg px-3 py-2 shadow-sm transition-all text-sm z-10"
      >
        <svg className="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
        </svg>
        На главную
      </Link>

      <div className="max-w-md w-full">
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5 }}
          className="bg-white rounded-2xl shadow-2xl p-8"
        >
          <div className="text-center mb-8">
            <div className="w-16 h-16 bg-gradient-to-br from-blue-500 to-cyan-500 rounded-xl flex items-center justify-center mx-auto mb-4">
              <Lock className="text-white" size={28} />
            </div>
            <h1 className="text-3xl font-bold text-gray-900">Вход в ТБанк</h1>
            <p className="text-gray-600 mt-2">Войдите по номеру телефона</p>
            
            {registered && (
              <div className="mt-4 p-3 bg-green-50 border border-green-200 rounded-lg">
                <p className="text-green-600 text-sm">
                  Регистрация успешна! Теперь войдите в систему.
                </p>
              </div>
            )}
          </div>

          {error && (
            <div className="mb-6 p-4 bg-red-50 border border-red-200 rounded-lg">
              <p className="text-red-600 text-sm">{error}</p>
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-6">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Номер телефона
              </label>
              <div className="relative">
                <Phone className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" size={20} />
                <input
                  type="tel"
                  value={phone}
                  onChange={handlePhoneChange}
                  className="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all"
                  placeholder="+7 (999) 999-99-99"
                  required
                  disabled={loading}
                />
              </div>
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Пароль
              </label>
              <div className="relative">
                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  className="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 hover:text-gray-600"
                  disabled={loading}
                >
                  {showPassword ? <EyeOff size={20} /> : <Eye size={20} />}
                </button>
                <input
                  type={showPassword ? "text" : "password"}
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  className="w-full pl-4 pr-10 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all"
                  placeholder="••••••••"
                  required
                  disabled={loading}
                />
              </div>
            </div>

            <div className="flex items-center justify-between">
              <label className="flex items-center">
                <input 
                  type="checkbox" 
                  className="w-4 h-4 text-blue-500 rounded" 
                  disabled={loading}
                />
                <span className="ml-2 text-sm text-gray-700">Запомнить меня</span>
              </label>
              <Link 
                href="/auth/forgot-password" 
                className="text-sm text-blue-500 hover:text-blue-700"
              >
                Забыли пароль?
              </Link>
            </div>

            <button
              type="submit"
              disabled={loading}
              className="w-full py-3 bg-gradient-to-r from-blue-500 to-cyan-500 text-white font-semibold rounded-lg hover:shadow-lg transition-all duration-300 disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center"
            >
              {loading ? (
                <>
                  <Loader2 className="animate-spin mr-2" size={20} />
                  Вход...
                </>
              ) : (
                'Войти'
              )}
            </button>
          </form>

          <div className="mt-6 pt-6 border-t border-gray-200">
            <p className="text-center text-gray-600">
              Нет аккаунта?{' '}
              <Link 
                href="/auth/register" 
                className="text-blue-500 hover:text-blue-700 font-semibold"
              >
                Зарегистрироваться
              </Link>
            </p>
          </div>
        </motion.div>
      </div>
    </div>
  )
}