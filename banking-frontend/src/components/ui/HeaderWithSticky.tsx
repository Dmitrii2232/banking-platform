'use client'

import { useState, useEffect, useRef } from 'react'
import { motion, AnimatePresence } from 'framer-motion'
import Link from 'next/link'
import { 
  Menu, X, CreditCard, Wallet, TrendingUp, User, 
  Bell, Search, ChevronDown, Shield, Globe, Phone,
  Home, Percent, PiggyBank, BarChart3, HelpCircle
} from 'lucide-react'
import Button from './Button'

export default function HeaderWithSticky() {
  const [isMenuOpen, setIsMenuOpen] = useState(false)
  const [scrolled, setScrolled] = useState(false)
  const [activeDropdown, setActiveDropdown] = useState<string | null>(null)

  useEffect(() => {
    const handleScroll = () => {
      setScrolled(window.scrollY > 50)
    }
    window.addEventListener('scroll', handleScroll)
    return () => window.removeEventListener('scroll', handleScroll)
  }, [])

  const services = [
    {
      title: 'Карты',
      icon: CreditCard,
      items: [
        { name: 'Дебетовые карты', description: 'Кэшбэк до 10%', color: 'blue' },
        { name: 'Кредитные карты', description: 'Льготный период', color: 'purple' },
        { name: 'Виртуальные карты', description: 'Для онлайн-покупок', color: 'green' },
        { name: 'Премиум карты', description: 'Особые привилегии', color: 'gold' }
      ]
    },
    {
      title: 'Кредиты',
      icon: Percent,
      items: [
        { name: 'Кредит наличными', description: 'До 5 млн ₽', color: 'blue' },
        { name: 'Ипотека', description: 'От 5.9% годовых', color: 'green' },
        { name: 'Автокредит', description: 'Первоначальный взнос 10%', color: 'red' },
        { name: 'Рефинансирование', description: 'Снижение ставки', color: 'purple' }
      ]
    },
    {
      title: 'Вклады',
      icon: PiggyBank,
      items: [
        { name: 'Накопительный счет', description: 'До 8% годовых', color: 'green' },
        { name: 'Срочный вклад', description: 'Фиксированная ставка', color: 'blue' },
        { name: 'Вклад для бизнеса', description: 'Для юридических лиц', color: 'purple' },
        { name: 'Мультивалютный вклад', description: 'В разных валютах', color: 'gold' }
      ]
    },
    {
      title: 'Инвестиции',
      icon: BarChart3,
      items: [
        { name: 'Брокерский счет', description: 'Торговля на бирже', color: 'blue' },
        { name: 'ИИС', description: 'Налоговые вычеты', color: 'green' },
        { name: 'Доверительное управление', description: 'Профессиональные управляющие', color: 'purple' },
        { name: 'Структурные продукты', description: 'Защита капитала', color: 'gold' }
      ]
    }
  ]

  return (
    <motion.header
      initial={{ y: -100 }}
      animate={{ y: 0 }}
      transition={{ type: 'spring', stiffness: 100, damping: 20 }}
      className={`fixed w-full z-50 transition-all duration-300 ${
        scrolled 
          ? 'bg-white/95 backdrop-blur-lg shadow-lg py-2' 
          : 'bg-white/90 backdrop-blur-sm py-3'
      }`}
    >
      <div className="container mx-auto px-4">
        <div className="flex items-center justify-between h-16">
          {/* Логотип */}
          <motion.div
            whileHover={{ scale: 1.05 }}
            className="flex items-center space-x-3"
          >
            <Link href="/" className="flex items-center space-x-3">
              <div className="relative">
                <div className="w-10 h-10 bg-gradient-to-br from-blue-600 to-cyan-500 rounded-xl flex items-center justify-center shadow-lg">
                  <CreditCard className="text-white" size={20} />
                </div>
              </div>
              <div>
                <h1 className="text-2xl font-bold bg-gradient-to-r from-blue-600 to-cyan-500 bg-clip-text text-transparent">
                  ТБанк
                </h1>
              </div>
            </Link>
          </motion.div>

          {/* Десктоп навигация */}
          <nav className="hidden lg:flex items-center space-x-1">
            <Link href="/">
              <Button variant="ghost" leftIcon={Home}>
                Главная
              </Button>
            </Link>
            
            {services.map((service) => (
              <div
                key={service.title}
                className="relative"
                onMouseEnter={() => setActiveDropdown(service.title)}
                onMouseLeave={() => setActiveDropdown(null)}
              >
                <Button
                  variant="ghost"
                  leftIcon={service.icon}
                  rightIcon={ChevronDown}
                  className={`transition-all ${
                    activeDropdown === service.title ? 'text-blue-600 bg-blue-50' : ''
                  }`}
                >
                  {service.title}
                </Button>

                {/* Мега-меню */}
                <AnimatePresence>
                  {activeDropdown === service.title && (
                    <motion.div
                      initial={{ opacity: 0, y: 10 }}
                      animate={{ opacity: 1, y: 0 }}
                      exit={{ opacity: 0, y: 10 }}
                      className="absolute top-full left-0 mt-2 w-96 bg-white rounded-2xl shadow-2xl border border-gray-100 overflow-hidden z-50"
                    >
                      <div className="p-6">
                        <div className="flex items-center space-x-3 mb-6">
                          <div className="p-2 rounded-lg bg-blue-100 text-blue-600">
                            <service.icon size={20} />
                          </div>
                          <h3 className="text-xl font-bold">{service.title}</h3>
                        </div>
                        
                        <div className="grid grid-cols-2 gap-4">
                          {service.items.map((item, index) => (
                            <motion.div
                              key={item.name}
                              initial={{ opacity: 0, x: -10 }}
                              animate={{ opacity: 1, x: 0 }}
                              transition={{ delay: index * 0.05 }}
                            >
                              <Link
                                href="#"
                                className="group block p-4 rounded-xl hover:bg-blue-50 transition-all duration-300"
                              >
                                <div className="flex items-start space-x-3">
                                  <div className={`w-3 h-3 rounded-full mt-1 bg-${item.color}-500`}></div>
                                  <div>
                                    <h4 className="font-semibold text-gray-900 group-hover:text-blue-600">
                                      {item.name}
                                    </h4>
                                    <p className="text-sm text-gray-600 mt-1">
                                      {item.description}
                                    </p>
                                  </div>
                                </div>
                              </Link>
                            </motion.div>
                          ))}
                        </div>

                        <div className="mt-6 pt-6 border-t">
                          <Link href="#" className="flex items-center justify-center text-blue-600 hover:text-blue-700">
                            <span>Все продукты {service.title}</span>
                            <ChevronDown className="ml-2 rotate-90" size={16} />
                          </Link>
                        </div>
                      </div>
                    </motion.div>
                  )}
                </AnimatePresence>
              </div>
            ))}

            <Link href="/help">
              <Button variant="ghost" leftIcon={HelpCircle}>
                Помощь
              </Button>
            </Link>
          </nav>

          {/* Правая часть */}
          <div className="hidden lg:flex items-center space-x-4">
            <button className="p-2 hover:bg-gray-100 rounded-lg transition-colors">
              <Search size={20} />
            </button>
            
            <Link href="/auth/login">
              <Button variant="outline" size="sm">
                Войти
              </Button>
            </Link>
            
            <Link href="/auth/register">
              <Button variant="primary" gradient size="md">
                Открыть счет
              </Button>
            </Link>
          </div>

          {/* Мобильная кнопка меню */}
          <button
            className="lg:hidden p-2"
            onClick={() => setIsMenuOpen(!isMenuOpen)}
          >
            {isMenuOpen ? <X size={24} /> : <Menu size={24} />}
          </button>
        </div>
      </div>

      {/* Мобильное мега-меню */}
      <AnimatePresence>
        {isMenuOpen && (
          <motion.div
            initial={{ opacity: 0, y: -20 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: -20 }}
            className="lg:hidden absolute top-full left-0 right-0 bg-white border-t shadow-2xl"
          >
            <div className="container mx-auto px-4 py-6">
              {/* Поиск */}
              <div className="relative mb-6">
                <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" size={20} />
                <input
                  type="text"
                  placeholder="Поиск услуг..."
                  className="w-full pl-10 pr-4 py-3 bg-gray-50 rounded-xl border focus:ring-2 focus:ring-blue-500"
                />
              </div>

              {/* Услуги */}
              <div className="space-y-4">
                {services.map((service) => {
                  const ServiceIcon = service.icon
                  return (
                    <div key={service.title} className="border rounded-xl overflow-hidden">
                      <button
                        className="w-full p-4 flex items-center justify-between bg-gray-50"
                        onClick={() => setActiveDropdown(
                          activeDropdown === service.title ? null : service.title
                        )}
                      >
                        <div className="flex items-center space-x-3">
                          <div className="p-2 rounded-lg bg-blue-100 text-blue-600">
                            <ServiceIcon size={20} />
                          </div>
                          <span className="font-semibold">{service.title}</span>
                        </div>
                        <ChevronDown
                          className={`transition-transform ${
                            activeDropdown === service.title ? 'rotate-180' : ''
                          }`}
                          size={20}
                        />
                      </button>
                      
                      <AnimatePresence>
                        {activeDropdown === service.title && (
                          <motion.div
                            initial={{ height: 0, opacity: 0 }}
                            animate={{ height: 'auto', opacity: 1 }}
                            exit={{ height: 0, opacity: 0 }}
                            className="overflow-hidden"
                          >
                            <div className="p-4 space-y-3">
                              {service.items.map((item) => (
                                <Link
                                  key={item.name}
                                  href="#"
                                  className="block p-3 rounded-lg hover:bg-blue-50"
                                  onClick={() => setIsMenuOpen(false)}
                                >
                                  <div className="flex items-center space-x-3">
                                    <div className={`w-2 h-2 rounded-full bg-${item.color}-500`}></div>
                                    <div>
                                      <div className="font-medium">{item.name}</div>
                                      <div className="text-sm text-gray-600">{item.description}</div>
                                    </div>
                                  </div>
                                </Link>
                              ))}
                            </div>
                          </motion.div>
                        )}
                      </AnimatePresence>
                    </div>
                  )
                })}
              </div>

              {/* Кнопки действий */}
              <div className="mt-6 pt-6 border-t space-y-3">
                <Link href="/auth/login" onClick={() => setIsMenuOpen(false)}>
                  <Button variant="outline" fullWidth>
                    Войти
                  </Button>
                </Link>
                <Link href="/auth/register" onClick={() => setIsMenuOpen(false)}>
                  <Button variant="primary" gradient fullWidth>
                    Открыть счет
                  </Button>
                </Link>
              </div>
            </div>
          </motion.div>
        )}
      </AnimatePresence>
    </motion.header>
  )
}