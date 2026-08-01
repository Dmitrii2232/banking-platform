'use client'

import { motion } from 'framer-motion'
import { 
  Phone, Mail, MapPin, Shield, Clock, 
  Facebook, Twitter, Instagram, Youtube, 
  Linkedin, ArrowUp, CreditCard, Globe,
  Award, Users, FileText, ChevronRight
} from 'lucide-react'
import Link from 'next/link'
import { usePathname } from 'next/navigation'
import { useState } from 'react'

export default function Footer() {
  const [hoveredLink, setHoveredLink] = useState<string | null>(null)
  const [expandedSections, setExpandedSections] = useState<Record<string, boolean>>({})
  const pathname = usePathname()

  const scrollToTop = () => {
    window.scrollTo({ top: 0, behavior: 'smooth' })
  }

  const toggleSection = (section: string) => {
    setExpandedSections(prev => ({
      ...prev,
      [section]: !prev[section]
    }))
  }

  const socialLinks = [
    { icon: <Facebook size={20} />, href: '#', label: 'Facebook', color: 'hover:bg-blue-600' },
    { icon: <Twitter size={20} />, href: '#', label: 'Twitter', color: 'hover:bg-blue-400' },
    { icon: <Instagram size={20} />, href: '#', label: 'Instagram', color: 'hover:bg-pink-600' },
    { icon: <Youtube size={20} />, href: '#', label: 'YouTube', color: 'hover:bg-red-600' },
    { icon: <Linkedin size={20} />, href: '#', label: 'LinkedIn', color: 'hover:bg-blue-700' },
  ]

  const quickLinks = [
    {
      title: 'О банке',
      links: [
        { name: 'История банка', href: '/about/history' },
        { name: 'Руководство', href: '/about/leadership' },
        { name: 'Лицензии и сертификаты', href: '/about/licenses' },
        { name: 'Реквизиты', href: '/about/requisites' },
        { name: 'Вакансии', href: '/about/career' },
        { name: 'Новости', href: '/about/news' },
      ]
    },
    {
      title: 'Клиентам',
      links: [
        { name: 'Тарифы и условия', href: '/clients/tariffs' },
        { name: 'Документы и формы', href: '/clients/documents' },
        { name: 'Частые вопросы', href: '/clients/faq' },
        { name: 'Контакты отделений', href: '/clients/branches' },
        { name: 'Онлайн-помощник', href: '/clients/support' },
        { name: 'Обучение', href: '/clients/education' },
      ]
    },
    {
      title: 'Инвесторам',
      links: [
        { name: 'Финансовая отчетность', href: '/investors/reports' },
        { name: 'Раскрытие информации', href: '/investors/disclosure' },
        { name: 'Акционерам', href: '/investors/shareholders' },
        { name: 'Кредитный рейтинг', href: '/investors/rating' },
        { name: 'Дивиденды', href: '/investors/dividends' },
        { name: 'Стратегия развития', href: '/investors/strategy' },
      ]
    },
    {
      title: 'Бизнесу',
      links: [
        { name: 'Расчетный счет', href: '/business/account' },
        { name: 'Бизнес-карты', href: '/business/cards' },
        { name: 'Торговый эквайринг', href: '/business/acquiring' },
        { name: 'Бизнес-кредиты', href: '/business/credits' },
        { name: 'Зарплатные проекты', href: '/business/salary' },
        { name: 'Интернет-банк для бизнеса', href: '/business/internet-bank' },
      ]
    }
  ]

  const contacts = [
    {
      icon: <Phone className="text-blue-400" size={20} />,
      title: 'Телефон',
      value: '8-800-555-35-35',
      description: 'Круглосуточно, бесплатно по России'
    },
    {
      icon: <Mail className="text-blue-400" size={20} />,
      title: 'Email',
      value: 'info@tbank.ru',
      description: 'Для общих вопросов'
    },
    {
      icon: <Clock className="text-blue-400" size={20} />,
      title: 'Часы работы',
      value: 'Круглосуточно',
      description: 'Онлайн-банк и поддержка'
    },
  ]

  const achievements = [
    { icon: <Award size={16} />, text: 'Лучший банк 2023' },
    { icon: <Users size={16} />, text: '2 млн+ клиентов' },
    { icon: <CreditCard size={16} />, text: 'Лидер по картам' },
    { icon: <Shield size={16} />, text: 'Высший рейтинг безопасности' },
  ]

  if (pathname === '/login' || 
      pathname === '/register' || 
      pathname.startsWith('/auth/') ||
      pathname === '/dashboard' || 
      pathname === '/transfers') {
    return null;
  }


  return (
    <footer className="bg-gradient-to-b from-gray-900 to-black text-white relative overflow-hidden z-10">
      {/* Анимированный фон */}
      <div className="absolute inset-0 opacity-10">
        <div className="absolute top-0 left-0 w-96 h-96 bg-blue-500 rounded-full blur-3xl -translate-x-1/2 -translate-y-1/2"></div>
        <div className="absolute bottom-0 right-0 w-96 h-96 bg-cyan-500 rounded-full blur-3xl translate-x-1/2 translate-y-1/2"></div>
      </div>

      {/* Кнопка наверх */}
      <motion.button
        whileHover={{ scale: 1.1 }}
        whileTap={{ scale: 0.9 }}
        onClick={scrollToTop}
        className="fixed bottom-6 right-6 w-12 h-12 bg-gradient-to-r from-blue-600 to-cyan-500 rounded-full shadow-lg hover:shadow-xl transition-all duration-300 z-50 flex items-center justify-center group"
      >
        <ArrowUp size={24} />
        <span className="absolute -top-8 right-0 bg-gray-900 text-white text-sm px-3 py-1 rounded-lg opacity-0 group-hover:opacity-100 transition-opacity whitespace-nowrap">
          Наверх
        </span>
      </motion.button>

      <div className="container mx-auto px-4 py-12 relative z-10">
        {/* Верхняя часть */}
        <div className="grid grid-cols-1 lg:grid-cols-5 gap-8 mb-12">
          {/* Лого и описание */}
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            whileInView={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.5 }}
            viewport={{ once: true }}
            className="lg:col-span-2"
          >
            <div className="flex items-center space-x-3 mb-6">
              <div className="w-12 h-12 bg-gradient-to-br from-blue-500 to-cyan-500 rounded-xl flex items-center justify-center shadow-lg">
                <CreditCard className="text-white" size={24} />
              </div>
              <div>
                <h3 className="text-2xl font-bold">ТБанк</h3>
                <p className="text-gray-400">Надежность с 1998 года</p>
              </div>
            </div>
            <p className="text-gray-400 mb-6">
              Ведущий финансовый институт страны, предоставляющий полный спектр банковских услуг 
              для частных лиц и бизнеса. Более 20 лет на рынке, 2 млн+ клиентов и высочайший 
              уровень сервиса.
            </p>
            
            {/* Достижения */}
            <div className="grid grid-cols-2 gap-3 mb-6">
              {achievements.map((achievement, index) => (
                <motion.div
                  key={index}
                  initial={{ opacity: 0, x: -20 }}
                  whileInView={{ opacity: 1, x: 0 }}
                  transition={{ delay: index * 0.1 }}
                  className="flex items-center space-x-2 text-sm text-gray-300"
                >
                  {achievement.icon}
                  <span>{achievement.text}</span>
                </motion.div>
              ))}
            </div>
            
            {/* Социальные сети */}
            <div className="flex space-x-3">
              {socialLinks.map((social, index) => (
                <motion.a
                  key={social.label}
                  href={social.href}
                  initial={{ opacity: 0, scale: 0.5 }}
                  whileInView={{ opacity: 1, scale: 1 }}
                  transition={{ delay: index * 0.1 }}
                  whileHover={{ y: -5 }}
                  className={`w-10 h-10 bg-gray-800 ${social.color} rounded-full flex items-center justify-center transition-all duration-300`}
                  aria-label={social.label}
                >
                  {social.icon}
                </motion.a>
              ))}
            </div>
          </motion.div>

          {/* Быстрые ссылки */}
          {quickLinks.map((section, sectionIndex) => (
            <motion.div
              key={section.title}
              initial={{ opacity: 0, y: 20 }}
              whileInView={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.5, delay: sectionIndex * 0.1 }}
              viewport={{ once: true }}
              className="relative"
            >
              {/* Мобильный аккордеон */}
              <button
                className="lg:hidden w-full flex items-center justify-between mb-4 p-3 bg-gray-800/50 rounded-lg"
                onClick={() => toggleSection(section.title)}
              >
                <h4 className="text-lg font-semibold">{section.title}</h4>
                <ChevronRight 
                  className={`transition-transform ${expandedSections[section.title] ? 'rotate-90' : ''}`}
                  size={20}
                />
              </button>

              <h4 className="text-lg font-semibold mb-4 hidden lg:block">
                {section.title}
              </h4>
              
              <div className={`${expandedSections[section.title] ? 'block' : 'hidden lg:block'}`}>
                <ul className="space-y-3">
                  {section.links.map((link, linkIndex) => (
                    <motion.li
                      key={link.name}
                      initial={{ opacity: 0, x: -10 }}
                      whileInView={{ opacity: 1, x: 0 }}
                      transition={{ delay: linkIndex * 0.05 }}
                      onMouseEnter={() => setHoveredLink(link.name)}
                      onMouseLeave={() => setHoveredLink(null)}
                    >
                      <Link
                        href={link.href}
                        className="text-gray-400 hover:text-white transition-colors flex items-center group"
                      >
                        <div className={`w-1 h-1 bg-gray-600 rounded-full mr-3 group-hover:bg-blue-500 transition-all ${hoveredLink === link.name ? 'w-3' : ''}`}></div>
                        <span className="text-sm">{link.name}</span>
                        <ChevronRight className="ml-2 opacity-0 group-hover:opacity-100 transition-all transform group-hover:translate-x-1" size={14} />
                      </Link>
                    </motion.li>
                  ))}
                </ul>
              </div>
            </motion.div>
          ))}
        </div>

        {/* Контактная информация */}
        <motion.div
          initial={{ opacity: 0 }}
          whileInView={{ opacity: 1 }}
          transition={{ duration: 0.5 }}
          viewport={{ once: true }}
          className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-12 p-6 bg-gradient-to-r from-blue-900/30 to-cyan-900/30 rounded-2xl backdrop-blur-sm border border-blue-500/20"
        >
          {contacts.map((contact, index) => (
            <motion.div
              key={contact.title}
              initial={{ opacity: 0, y: 20 }}
              whileInView={{ opacity: 1, y: 0 }}
              transition={{ delay: index * 0.1 }}
              className="flex items-center space-x-4 p-4 hover:bg-white/5 rounded-xl transition-colors"
            >
              <div className="w-12 h-12 bg-blue-900/50 rounded-lg flex items-center justify-center">
                {contact.icon}
              </div>
              <div>
                <p className="text-sm text-gray-400">{contact.title}</p>
                <p className="text-xl font-semibold">{contact.value}</p>
                <p className="text-xs text-gray-500 mt-1">{contact.description}</p>
              </div>
            </motion.div>
          ))}
        </motion.div>

        {/* Дополнительные ссылки */}
        <motion.div
          initial={{ opacity: 0 }}
          whileInView={{ opacity: 1 }}
          transition={{ duration: 0.5 }}
          viewport={{ once: true }}
          className="flex flex-wrap justify-center gap-6 mb-8"
        >
          <Link href="#" className="text-gray-400 hover:text-white transition-colors text-sm">
            Карта сайта
          </Link>
          <Link href="#" className="text-gray-400 hover:text-white transition-colors text-sm">
            Политика конфиденциальности
          </Link>
          <Link href="#" className="text-gray-400 hover:text-white transition-colors text-sm">
            Условия использования
          </Link>
          <Link href="#" className="text-gray-400 hover:text-white transition-colors text-sm">
            Политика обработки данных
          </Link>
          <Link href="#" className="text-gray-400 hover:text-white transition-colors text-sm">
            Противодействие мошенничеству
          </Link>
          <Link href="#" className="text-gray-400 hover:text-white transition-colors text-sm">
            Антикоррупционная политика
          </Link>
        </motion.div>

        {/* Декларации и предупреждения */}
        <motion.div
          initial={{ opacity: 0 }}
          whileInView={{ opacity: 1 }}
          transition={{ duration: 0.5 }}
          viewport={{ once: true }}
          className="bg-gray-900/50 rounded-xl p-6 mb-8"
        >
          <div className="flex flex-col md:flex-row items-start md:items-center justify-between gap-4">
            <div className="flex items-center space-x-4">
              <Shield className="text-green-400" size={20} />
              <div>
                <p className="text-sm text-gray-400">
                  Лицензия Банка России на осуществление банковских операций №1234 от 01.01.1998
                </p>
                <p className="text-xs text-gray-500 mt-1">
                  Генеральная лицензия Банка России №9999 от 01.01.2024
                </p>
              </div>
            </div>
            <div className="flex items-center space-x-2 text-xs text-gray-500">
              <Globe size={14} />
              <span>ТБанк входит в систему страхования вкладов</span>
            </div>
          </div>
        </motion.div>

        {/* Нижняя часть */}
        <motion.div
          initial={{ opacity: 0 }}
          whileInView={{ opacity: 1 }}
          transition={{ duration: 0.5 }}
          viewport={{ once: true }}
          className="border-t border-gray-800 pt-8"
        >
          <div className="flex flex-col md:flex-row justify-between items-center gap-4">
            <div className="text-center md:text-left">
              <p className="text-gray-500 text-sm">
                © 1998-2024 ТБанк. Все права защищены.
              </p>
    
            </div>
            
            <div className="flex items-center space-x-4">
              <div className="flex items-center space-x-2">
                <div className="w-2 h-2 bg-green-500 rounded-full animate-pulse"></div>
                <span className="text-xs text-gray-500">Сервис работает стабильно</span>
              </div>
              <div className="text-xs text-gray-600">
                v1.0.0 • {new Date().getFullYear()}
              </div>
            </div>
          </div>

          {/* Мобильное приложение */}
          <div className="mt-8 pt-8 border-t border-gray-800">
            <div className="flex flex-col md:flex-row items-center justify-between gap-6">
              <div>
                <h4 className="text-lg font-semibold mb-2">Мобильное приложение</h4>
                <p className="text-gray-400 text-sm">
                  Скачайте приложение для удобного управления счетами
                </p>
              </div>
              <div className="flex gap-4">
                <button className="px-6 py-3 bg-gray-800 hover:bg-gray-700 rounded-xl transition-colors flex items-center space-x-2">
                  <div className="w-8 h-8 bg-white rounded-lg"></div>
                  <div className="text-left">
                    <div className="text-xs text-gray-400">Скачать в</div>
                    <div className="font-semibold">App Store</div>
                  </div>
                </button>
                <button className="px-6 py-3 bg-gray-800 hover:bg-gray-700 rounded-xl transition-colors flex items-center space-x-2">
                  <div className="w-8 h-8 bg-white rounded-lg"></div>
                  <div className="text-left">
                    <div className="text-xs text-gray-400">Скачать в</div>
                    <div className="font-semibold">Google Play</div>
                  </div>
                </button>
              </div>
            </div>
          </div>
        </motion.div>
      </div>

      {/* Анимированные элементы */}
      <div className="absolute bottom-0 left-0 right-0 h-1 bg-gradient-to-r from-blue-500 via-cyan-500 to-blue-500">
        <motion.div
          className="h-full bg-white"
          initial={{ width: '0%' }}
          animate={{ width: '100%' }}
          transition={{ duration: 2, repeat: Infinity, ease: 'linear' }}
        />
      </div>
    </footer>
  )
}