'use client'

import { useState, useEffect } from 'react'
import { motion, AnimatePresence } from 'framer-motion'
import Link from 'next/link'
import { 
  Menu, CreditCard, Wallet, TrendingUp, User, Search, ChevronDown, 
  LogIn, UserPlus, PiggyBank, BarChart3, Shield, Percent, 
  Home, Car, GraduationCap, Building2, Briefcase, Globe,
  Smartphone, Wifi, Plane, Heart, Gem, ShoppingBag
} from 'lucide-react'
import { usePathname } from 'next/navigation'

export default function Header() {
  const [isMenuOpen, setIsMenuOpen] = useState(false)
  const [scrolled, setScrolled] = useState(false)
  const [activeDropdown, setActiveDropdown] = useState<string | null>(null)
  const pathname = usePathname()

  useEffect(() => {
    const handleScroll = () => setScrolled(window.scrollY > 20)
    window.addEventListener('scroll', handleScroll)
    return () => window.removeEventListener('scroll', handleScroll)
  }, [])

  if (pathname === '/login' || 
      pathname === '/register' || 
      pathname.startsWith('/auth/') ||
      pathname === '/dashboard' || 
      pathname === '/transfers') {
    return null
  }
  const DollarSign = ({ size, className }: { size: number; className?: string }) => (
    <svg className={className} width={size} height={size} viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <line x1="12" y1="1" x2="12" y2="23"></line>
      <path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"></path>
    </svg>
  )

  const PieChart = ({ size, className }: { size: number; className?: string }) => (
    <svg className={className} width={size} height={size} viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M21.21 15.89A10 10 0 1 1 8 2.83"></path>
      <path d="M22 12A10 10 0 0 0 12 2v10z"></path>
    </svg>
  )

  const navItems = [
    { 
      href: '/', 
      label: 'Главная', 
      icon: <Wallet size={18} /> 
    },
    { 
      label: 'Карты', 
      icon: <CreditCard size={18} />,
      dropdown: [
        { label: 'Дебетовые карты', icon: <CreditCard size={14} />, desc: 'Бесплатное обслуживание' },
        { label: 'Кредитные карты', icon: <Percent size={14} />, desc: 'Льготный период 120 дней' },
        { label: 'Премиум карты', icon: <Gem size={14} />, desc: 'Персональный консьерж' },
        { label: 'Виртуальные карты', icon: <Smartphone size={14} />, desc: 'Мгновенный выпуск' },
        { label: 'Детские карты', icon: <Heart size={14} />, desc: 'Родительский контроль' },
        { label: 'Зарплатные карты', icon: <Briefcase size={14} />, desc: 'Для бизнеса' },
      ]
    },
    { 
      label: 'Кредиты', 
      icon: <TrendingUp size={18} />,
      dropdown: [
        { label: 'Кредит наличными', icon: <DollarSign size={14} />, desc: 'До 5 млн ₽, от 5.9%' },
        { label: 'Ипотека', icon: <Home size={14} />, desc: 'На новостройки и вторичку' },
        { label: 'Автокредит', icon: <Car size={14} />, desc: 'На новые и подержанные' },
        { label: 'Рефинансирование', icon: <BarChart3 size={14} />, desc: 'Объедините кредиты' },
        { label: 'Образовательный', icon: <GraduationCap size={14} />, desc: 'На обучение' },
        { label: 'Бизнес-кредит', icon: <Building2 size={14} />, desc: 'Для ИП и ООО' },
      ]
    },
    { 
      label: 'Вклады', 
      icon: <PiggyBank size={18} />,
      dropdown: [
        { label: 'Накопительный счёт', icon: <Wallet size={14} />, desc: 'Свободное пополнение' },
        { label: 'Срочный вклад', icon: <PiggyBank size={14} />, desc: 'Максимальный процент' },
        { label: 'Вклад в юанях', icon: <Globe size={14} />, desc: 'Валютная диверсификация' },
        { label: 'Инвестиционный вклад', icon: <BarChart3 size={14} />, desc: 'С защитой капитала' },
      ]
    },
    { 
      label: 'Инвестиции', 
      icon: <BarChart3 size={18} />,
      dropdown: [
        { label: 'Брокерский счёт', icon: <TrendingUp size={14} />, desc: 'Акции, облигации, ETF' },
        { label: 'ИИС', icon: <Shield size={14} />, desc: 'Налоговый вычет до 52 000 ₽' },
        { label: 'ПИФы', icon: <PieChart size={14} />, desc: 'Готовые стратегии' },
        { label: 'Доверительное управление', icon: <Briefcase size={14} />, desc: 'Профессиональный управляющий' },
        { label: 'Структурные ноты', icon: <Shield size={14} />, desc: 'Защита капитала + доходность' },
      ]
    },
    { 
      label: 'Ещё', 
      //icon: <ChevronDown size={18} />,
      dropdown: [
        { label: 'Страхование', icon: <Shield size={14} />, desc: 'Жизнь, здоровье, имущество' },
        { label: 'Премиум-обслуживание', icon: <Gem size={14} />, desc: 'Персональный банкир' },
        { label: 'Обмен валюты', icon: <Globe size={14} />, desc: 'Выгодный курс' },
        { label: 'Интернет-банк', icon: <Wifi size={14} />, desc: 'Веб-версия' },
      ]
    },
  ]

  

  return (
    <motion.header
      initial={{ y: -100 }}
      animate={{ y: 0 }}
      className={`fixed w-full z-50 transition-all duration-300 ${
        scrolled ? 'bg-white/95 backdrop-blur-lg shadow-lg py-2' : 'bg-white py-3'
      }`}
    >
      <div className="container mx-auto px-4">
        <div className="flex justify-between items-center h-14">
          {/* Динамический логотип */}
<Link href="/" className="flex items-center space-x-2 flex-shrink-0 group">
  <motion.div 
    className="w-9 h-9 bg-gradient-to-br from-blue-600 to-cyan-500 rounded-xl flex items-center justify-center shadow-lg relative overflow-hidden"
    whileHover={{ scale: 1.1, rotate: 5 }}
    whileTap={{ scale: 0.95 }}
  >
    <CreditCard className="text-white relative z-10" size={18} />
    {/* Свечение */}
    <motion.div 
      className="absolute inset-0 bg-white/20 rounded-xl"
      animate={{ opacity: [0, 0.3, 0] }}
      transition={{ duration: 2, repeat: Infinity }}
    />
  </motion.div>
  
  <div>
    <motion.span 
      className="text-xl font-bold bg-gradient-to-r from-blue-600 to-cyan-500 bg-clip-text text-transparent block"
      whileHover={{ scale: 1.05 }}
    >
      ТБанк
    </motion.span>
    <motion.span 
      className="text-[10px] text-gray-400 block leading-none"
      initial={{ opacity: 0 }}
      animate={{ opacity: scrolled ? 0 : 1 }}
    >
      Надёжность с 1998
    </motion.span>
  </div>
</Link>

          {/* Навигация */}
          <nav className="hidden lg:flex items-center space-x-0">
            {navItems.map((item) => (
              <div
                key={item.label}
                className="relative"
                onMouseEnter={() => item.dropdown && setActiveDropdown(item.label)}
                onMouseLeave={() => setActiveDropdown(null)}
              >
                {item.href ? (
                  <Link
                    href={item.href}
                    className="px-3 py-2 rounded-lg text-gray-700 hover:text-blue-600 hover:bg-blue-50 transition-all flex items-center space-x-1.5 text-sm font-medium"
                  >
                    {item.icon}
                    <span>{item.label}</span>
                  </Link>
                ) : (
                  <button
                    className="px-3 py-2 rounded-lg text-gray-700 hover:text-blue-600 hover:bg-blue-50 transition-all flex items-center space-x-1.5 text-sm font-medium"
                  >
                    {item.icon}
                    <span>{item.label}</span>
                    {item.dropdown && <ChevronDown size={14} className={activeDropdown === item.label ? 'rotate-180 transition-transform' : 'transition-transform'} />}
                  </button>
                )}

                {/* Mega Dropdown */}
                <AnimatePresence>
                  {item.dropdown && activeDropdown === item.label && (
                    <motion.div
                      initial={{ opacity: 0, y: 5 }}
                      animate={{ opacity: 1, y: 0 }}
                      exit={{ opacity: 0, y: 5 }}
                      transition={{ duration: 0.15 }}
                      className="absolute top-full left-0 mt-1 w-72 bg-white rounded-xl shadow-2xl border overflow-hidden z-50"
                    >
                      <div className="p-2">
                        {item.dropdown.map((d, i) => (
                          <Link
                            key={i}
                            href="#"
                            className="flex items-start space-x-3 px-3 py-2.5 rounded-lg hover:bg-blue-50 transition-colors group"
                          >
                            <div className="w-8 h-8 rounded-lg bg-gray-100 flex items-center justify-center flex-shrink-0 mt-0.5 group-hover:bg-blue-100 transition-colors">
                              {d.icon}
                            </div>
                            <div>
                              <div className="text-sm font-medium text-gray-800 group-hover:text-blue-600 transition-colors">
                                {d.label}
                              </div>
                              <div className="text-xs text-gray-500 mt-0.5">{d.desc}</div>
                            </div>
                          </Link>
                        ))}
                      </div>
                    </motion.div>
                  )}
                </AnimatePresence>
              </div>
            ))}
          </nav>

          {/* Личный кабинет */}
          <div 
            className="hidden lg:block relative"
            onMouseEnter={() => setActiveDropdown('user')}
            onMouseLeave={() => setActiveDropdown(null)}
          >
            <button className="flex items-center space-x-2 px-3 py-2 rounded-lg bg-gray-100 hover:bg-gray-200 transition-colors text-sm font-medium text-gray-700">
              <User size={18} />
              <span>Личный кабинет</span>
            </button>

            <AnimatePresence>
              {activeDropdown === 'user' && (
                <motion.div
                  initial={{ opacity: 0, y: 5 }}
                  animate={{ opacity: 1, y: 0 }}
                  exit={{ opacity: 0, y: 5 }}
                  transition={{ duration: 0.15 }}
                  className="absolute right-0 top-full mt-1 w-48 bg-white rounded-xl shadow-2xl border overflow-hidden z-50"
                >
                  <Link
                    href="/auth/login"
                    className="flex items-center space-x-3 px-4 py-3 text-sm text-gray-700 hover:bg-blue-50 hover:text-blue-600 transition-colors"
                  >
                    <LogIn size={16} />
                    <span>Войти</span>
                  </Link>
                  <Link
                    href="/auth/register"
                    className="flex items-center space-x-3 px-4 py-3 text-sm text-white bg-blue-500 hover:bg-blue-600 transition-colors"
                  >
                    <UserPlus size={16} />
                    <span>Открыть счёт</span>
                  </Link>
                </motion.div>
              )}
            </AnimatePresence>
          </div>

          {/* Мобильное меню */}
          <button className="lg:hidden p-2" onClick={() => setIsMenuOpen(!isMenuOpen)}>
            <Menu size={24} className="text-gray-700" />
          </button>
        </div>
      </div>

      {/* Мобильное меню */}
      <AnimatePresence>
        {isMenuOpen && (
          <motion.div
            initial={{ opacity: 0, height: 0 }}
            animate={{ opacity: 1, height: 'auto' }}
            exit={{ opacity: 0, height: 0 }}
            className="lg:hidden bg-white shadow-xl border-t overflow-y-auto max-h-[80vh]"
          >
            <div className="container mx-auto px-4 py-4 space-y-1">
              {navItems.map((item) => (
                <div key={item.label}>
                  <Link
                    href={item.href || '#'}
                    className="flex items-center space-x-3 p-3 rounded-xl hover:bg-gray-50 font-medium"
                    onClick={() => !item.href && setIsMenuOpen(false)}
                  >
                    {item.icon}
                    <span>{item.label}</span>
                    {item.dropdown && <ChevronDown size={14} className="ml-auto" />}
                  </Link>
                </div>
              ))}
              <div className="border-t pt-3 space-y-2">
                <Link href="/auth/login" className="flex items-center space-x-3 p-3 rounded-xl hover:bg-gray-50" onClick={() => setIsMenuOpen(false)}>
                  <LogIn size={18} /><span>Войти</span>
                </Link>
                <Link href="/auth/register" className="flex items-center space-x-3 p-3 rounded-xl bg-blue-500 text-white" onClick={() => setIsMenuOpen(false)}>
                  <UserPlus size={18} /><span>Открыть счёт</span>
                </Link>
              </div>
            </div>
          </motion.div>
        )}
      </AnimatePresence>
    </motion.header>
  )
}