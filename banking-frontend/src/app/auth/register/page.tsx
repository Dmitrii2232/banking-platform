'use client'

import { motion } from 'framer-motion'
import { User, Mail, Phone, MapPin, Calendar, Eye, EyeOff, CheckCircle } from 'lucide-react'
import Link from 'next/link'
import { useState } from 'react' // Добавьте этот импорт
import { useRouter } from 'next/navigation'
import { authService, RegisterRequest } from '@/lib/auth'

export default function RegisterPage() {
  const router = useRouter()
  const [showPassword, setShowPassword] = useState(false)
  const [step, setStep] = useState(1)
  const [loading, setLoading] = useState(false) // Добавьте это состояние
  const [error, setError] = useState('')
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    email: '',
    phone: '',
    password: '',
    confirmPassword: '',
    birthDate: '',
    address: '',
  })

  const steps = [
    { number: 1, title: 'Личные данные' },
    { number: 2, title: 'Контакты' },
    { number: 3, title: 'Безопасность' },
  ]

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target
    setFormData(prev => ({ ...prev, [name]: value }))
    setError('') // Сбрасываем ошибку при изменении поля
  }

  const handleNext = () => {
    // Валидация текущего шага
    if (step === 1) {
      if (!formData.firstName || !formData.lastName || !formData.birthDate) {
        setError('Пожалуйста, заполните все обязательные поля')
        return
      }
    } else if (step === 2) {
      if (!formData.email || !formData.phone) {
        setError('Пожалуйста, заполните все обязательные поля')
        return
      }
    }
    
    if (step < 3) setStep(step + 1)
    setError('')
  }

  const handlePrev = () => {
    if (step > 1) setStep(step - 1)
    setError('')
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setLoading(true) // Включаем loading
    setError('')

    // Проверка пароля
    if (formData.password !== formData.confirmPassword) {
      setError('Пароли не совпадают')
      setLoading(false) // Выключаем loading
      return
    }

    if (formData.password.length < 8) {
      setError('Пароль должен быть не менее 8 символов')
      setLoading(false) // Выключаем loading
      return
    }

    try {
      // Формируем запрос для отправки на сервер
      const registerData: RegisterRequest = {
        phone: formData.phone,
        email: formData.email,
        password: formData.password,
        firstName: formData.firstName,
        lastName: formData.lastName,
        phoneNumber: formData.phone,
        birthDate: formData.birthDate || undefined,
        address: formData.address || undefined,
      }

      console.log('Отправка данных регистрации:', registerData)

      // Используем authService для регистрации
      const result = await authService.register(registerData)
      
      console.log('Результат регистрации:', result)

      if (result.success) {
        // Успешная регистрация
        if (result.data) {
          // Автоматический вход прошел успешно
          console.log('Автоматический вход после регистрации успешен')
          router.push('/dashboard')
          router.refresh()
        } else {
          // Регистрация успешна, но нужен вход
          console.log('Регистрация успешна, требуется вход')
          router.push('/auth/login?registered=true&email=' + encodeURIComponent(formData.email))
        }
      } else {
        // Ошибка регистрации
        setError(result.message || 'Ошибка регистрации')
      }
    } catch (err: any) {
      console.error('Registration error:', err)
      setError(err.message || 'Произошла ошибка при регистрации')
    } finally {
      setLoading(false) // Выключаем loading в любом случае
    }
  }

  const benefits = [
    'Бесплатное открытие счета',
    'Персональный менеджер',
    'Кэшбэк до 10%',
    'Мобильное приложение',
    'Круглосуточная поддержка',
  ]

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-cyan-50 py-12 px-4 relative">
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
      <div className="max-w-6xl mx-auto">
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5 }}
          className="grid grid-cols-1 lg:grid-cols-2 gap-8"
        >
          {/* Левая колонка - форма */}
          <div className="bg-white rounded-2xl shadow-2xl p-8">
            <div className="mb-8">
              <div className="flex items-center justify-between mb-6">
                <div>
                  <h1 className="text-3xl font-bold text-gray-900">Откройте счет в ТБанк</h1>
                  <p className="text-gray-600 mt-2">Всего за 5 минут онлайн</p>
                </div>
                <div className="w-12 h-12 bg-gradient-to-br from-blue-500 to-cyan-500 rounded-xl flex items-center justify-center">
                  <User className="text-white" size={24} />
                </div>
              </div>

              {/* Шаги */}
              <div className="flex justify-between mb-8">
                {steps.map((s) => (
                  <div key={s.number} className="flex items-center">
                    <div className={`w-8 h-8 rounded-full flex items-center justify-center ${
                      step >= s.number 
                        ? 'bg-blue-500 text-white' 
                        : 'bg-gray-200 text-gray-500'
                    }`}>
                      {s.number}
                    </div>
                    <span className={`ml-2 text-sm ${step >= s.number ? 'font-semibold' : 'text-gray-500'}`}>
                      {s.title}
                    </span>
                    {s.number < steps.length && (
                      <div className="w-12 h-0.5 bg-gray-200 mx-4"></div>
                    )}
                  </div>
                ))}
              </div>
            </div>

            {error && (
              <div className="mb-6 p-4 bg-red-50 border border-red-200 rounded-lg">
                <p className="text-red-600 text-sm">{error}</p>
              </div>
            )}

            <form onSubmit={handleSubmit} className="space-y-6">
              {step === 1 && (
                <motion.div
                  initial={{ opacity: 0, x: -20 }}
                  animate={{ opacity: 1, x: 0 }}
                  className="space-y-6"
                >
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">
                        Имя *
                      </label>
                      <div className="relative">
                        <User className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" size={20} />
                        <input
                          type="text"
                          name="firstName"
                          value={formData.firstName}
                          onChange={handleInputChange}
                          className="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                          placeholder="Иван"
                          required
                          disabled={loading}
                        />
                      </div>
                    </div>

                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">
                        Фамилия *
                      </label>
                      <input
                        type="text"
                        name="lastName"
                        value={formData.lastName}
                        onChange={handleInputChange}
                        className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                        placeholder="Иванов"
                        required
                        disabled={loading}
                      />
                    </div>
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      Дата рождения *
                    </label>
                    <div className="relative">
                      <Calendar className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" size={20} />
                      <input
                        type="date"
                        name="birthDate"
                        value={formData.birthDate}
                        onChange={handleInputChange}
                        className="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                        required
                        disabled={loading}
                      />
                    </div>
                  </div>
                </motion.div>
              )}

              {step === 2 && (
                <motion.div
                  initial={{ opacity: 0, x: -20 }}
                  animate={{ opacity: 1, x: 0 }}
                  className="space-y-6"
                >
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      Электронная почта *
                    </label>
                    <div className="relative">
                      <Mail className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" size={20} />
                      <input
                        type="email"
                        name="email"
                        value={formData.email}
                        onChange={handleInputChange}
                        className="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                        placeholder="you@example.com"
                        required
                        disabled={loading}
                      />
                    </div>
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      Телефон *
                    </label>
                    <div className="relative">
                      <Phone className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" size={20} />
                      <input
                        type="tel"
                        name="phone"
                        value={formData.phone}
                        onChange={handleInputChange}
                        className="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                        placeholder="+7 (999) 999-99-99"
                        required
                        disabled={loading}
                      />
                    </div>
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      Адрес
                    </label>
                    <div className="relative">
                      <MapPin className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" size={20} />
                      <input
                        type="text"
                        name="address"
                        value={formData.address}
                        onChange={handleInputChange}
                        className="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                        placeholder="г. Москва, ул. Примерная, д. 1"
                        disabled={loading}
                      />
                    </div>
                  </div>
                </motion.div>
              )}

              {step === 3 && (
                <motion.div
                  initial={{ opacity: 0, x: -20 }}
                  animate={{ opacity: 1, x: 0 }}
                  className="space-y-6"
                >
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      Пароль *
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
                        name="password"
                        value={formData.password}
                        onChange={handleInputChange}
                        className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                        placeholder="Не менее 8 символов"
                        required
                        disabled={loading}
                      />
                    </div>
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      Подтвердите пароль *
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
                        name="confirmPassword"
                        value={formData.confirmPassword}
                        onChange={handleInputChange}
                        className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                        placeholder="Повторите пароль"
                        required
                        disabled={loading}
                      />
                    </div>
                  </div>

                  <div className="space-y-3">
                    <label className="flex items-center">
                      <input 
                        type="checkbox" 
                        className="w-4 h-4 text-blue-500 rounded" 
                        required 
                        disabled={loading}
                      />
                      <span className="ml-2 text-sm text-gray-700">
                        Я согласен с{' '}
                        <Link href="/terms" className="text-blue-500 hover:text-blue-700">
                          условиями использования
                        </Link>
                      </span>
                    </label>
                    <label className="flex items-center">
                      <input 
                        type="checkbox" 
                        className="w-4 h-4 text-blue-500 rounded" 
                        required 
                        disabled={loading}
                      />
                      <span className="ml-2 text-sm text-gray-700">
                        Я согласен на обработку персональных данных
                      </span>
                    </label>
                  </div>
                </motion.div>
              )}

              <div className="flex justify-between pt-6">
                {step > 1 ? (
                  <button
                    type="button"
                    onClick={handlePrev}
                    className="px-6 py-3 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                    disabled={loading}
                  >
                    Назад
                  </button>
                ) : (
                  <div></div>
                )}

                {step < 3 ? (
                  <button
                    type="button"
                    onClick={handleNext}
                    className="px-6 py-3 bg-blue-500 text-white rounded-lg hover:bg-blue-600 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                    disabled={loading}
                  >
                    Продолжить
                  </button>
                ) : (
                  <button
                    type="submit"
                    className="px-6 py-3 bg-gradient-to-r from-blue-500 to-cyan-500 text-white font-semibold rounded-lg hover:shadow-lg transition-all duration-300 disabled:opacity-50 disabled:cursor-not-allowed"
                    disabled={loading}
                  >
                    {loading ? 'Регистрация...' : 'Открыть счет'}
                  </button>
                )}
              </div>
            </form>

            <div className="mt-8 pt-8 border-t border-gray-200">
              <p className="text-center text-gray-600">
                Уже есть аккаунт?{' '}
                <Link href="/auth/login" className="text-blue-500 hover:text-blue-700 font-semibold">
                  Войти
                </Link>
              </p>
            </div>
          </div>

          {/* Правая колонка - преимущества */}
          <div className="space-y-6">
            <div className="bg-gradient-to-br from-blue-500 to-cyan-500 rounded-2xl p-8 text-white">
              <h2 className="text-2xl font-bold mb-6">Ваши преимущества</h2>
              <div className="space-y-4">
                {benefits.map((benefit, index) => (
                  <motion.div
                    key={index}
                    initial={{ opacity: 0, x: 20 }}
                    animate={{ opacity: 1, x: 0 }}
                    transition={{ delay: index * 0.1 }}
                    className="flex items-center"
                  >
                    <CheckCircle className="mr-3" size={20} />
                    <span>{benefit}</span>
                  </motion.div>
                ))}
              </div>
            </div>

            <div className="bg-white rounded-2xl p-8 shadow-lg">
              <h3 className="text-xl font-bold mb-4">Безопасность</h3>
              <ul className="space-y-3 text-gray-600">
                <li className="flex items-center">
                  <div className="w-2 h-2 bg-green-500 rounded-full mr-3"></div>
                  SSL-шифрование всех данных
                </li>
                <li className="flex items-center">
                  <div className="w-2 h-2 bg-green-500 rounded-full mr-3"></div>
                  Двухфакторная аутентификация
                </li>
                <li className="flex items-center">
                  <div className="w-2 h-2 bg-green-500 rounded-full mr-3"></div>
                  Страхование вкладов
                </li>
                <li className="flex items-center">
                  <div className="w-2 h-2 bg-green-500 rounded-full mr-3"></div>
                  Мониторинг подозрительных операций
                </li>
              </ul>
            </div>

            <div className="bg-gray-50 rounded-2xl p-8">
              <h3 className="text-xl font-bold mb-4">Поддержка 24/7</h3>
              <p className="text-gray-600 mb-4">
                Наша служба поддержки работает круглосуточно. 
                Готовы помочь в любое время.
              </p>
              <div className="text-2xl font-bold text-blue-500">8-800-555-35-35</div>
              <p className="text-sm text-gray-500 mt-1">Звонок бесплатный</p>
            </div>
          </div>
        </motion.div>
      </div>
    </div>
  )
}