'use client'

import { motion } from 'framer-motion'
import { ArrowRight, Shield, Zap, Globe, Users, CreditCard, Lock, Smartphone, CheckCircle } from 'lucide-react'
import Link from 'next/link'
import BannerCarousel from '@/components/BannerCarousel'
import InteractiveCard from '@/components/InteractiveCard'
import AnimatedStats from '@/components/AnimatedStats'
import FloatingElements from '@/components/FloatingElements'

export default function HomePage() {
  const features = [
    { icon: <Shield className="text-blue-500" size={32} />, title: 'Безопасность', description: 'Многоуровневая защита операций' },
    { icon: <Zap className="text-green-500" size={32} />, title: 'Скорость', description: 'Мгновенные переводы 24/7' },
    { icon: <Globe className="text-purple-500" size={32} />, title: 'Доступность', description: 'Банкинг в любой точке мира' },
    { icon: <Users className="text-orange-500" size={32} />, title: 'Поддержка', description: 'Персональный менеджер' },
    { icon: <Lock className="text-red-500" size={32} />, title: 'Конфиденциальность', description: 'Защита персональных данных' },
    { icon: <Smartphone className="text-indigo-500" size={32} />, title: 'Мобильность', description: 'Удобное мобильное приложение' },
  ]

  const services = [
    { title: 'Кредиты наличными', description: 'Без залога и поручителей', rate: 'от 5.9%', color: 'from-blue-500 to-cyan-500', features: ['До 5 млн ₽', 'Решение за 5 минут', 'Без справок'] },
    { title: 'Вклады', description: 'С капитализацией процентов', rate: 'до 8.5%', color: 'from-green-500 to-emerald-500', features: ['Пополняемый', 'Снятие без потерь', 'Страхование'] },
    { title: 'Ипотека', description: 'На первичное и вторичное жилье', rate: 'от 7.4%', color: 'from-purple-500 to-pink-500', features: ['Первоначальный взнос 15%', 'Срок до 30 лет', 'Онлайн одобрение'] },
    { title: 'Инвестиции', description: 'Доверительное управление', rate: 'до 25%', color: 'from-orange-500 to-yellow-500', features: ['От 10 000 ₽', 'Личный брокер', 'Аналитика онлайн'] },
  ]

  const steps = [
    { number: '01', title: 'Регистрация', desc: 'Откройте счет онлайн за 5 минут' },
    { number: '02', title: 'Верификация', desc: 'Подтвердите личность через приложение' },
    { number: '03', title: 'Пополнение', desc: 'Внесите средства удобным способом' },
    { number: '04', title: 'Использование', desc: 'Начинайте пользоваться услугами' },
  ]

  return (
    <div className="relative">
      {/* Карусель + квадраты */}
      <section className="relative h-[80vh] min-h-[500px]">
        
        <BannerCarousel />
      </section>

      {/* Блок под каруселью */}
      <section className="py-16 bg-gradient-to-b from-gray-50 to-white">
        <div className="container mx-auto px-4">
          <motion.div
            initial={{ opacity: 0, y: 30 }}
            whileInView={{ opacity: 1, y: 0 }}
            viewport={{ once: true }}
            className="w-full max-w-5xl mx-auto bg-white rounded-2xl shadow-xl border border-gray-100 p-8 md:p-10 -mt-24 relative z-20"
          >
            <div className="flex flex-col lg:flex-row items-center gap-8">
              <div className="flex-1">
                <div className="inline-flex items-center px-4 py-2 rounded-full bg-blue-50 mb-6">
                  <span className="text-sm font-medium text-blue-600">⭐ Рейтинг 4.9/5</span>
                  <span className="mx-2 text-blue-300">•</span>
                  <span className="text-sm text-blue-600">2 млн+ клиентов</span>
                </div>

                <h1 className="text-3xl md:text-4xl lg:text-5xl font-bold mb-4 text-gray-900">
                  Банк, который
                  <span className="text-blue-500"> растет с вами</span>
                </h1>

                <div className="mb-6">
                  <p className="text-lg text-gray-700 font-semibold mb-2">Кредитный лимит до 1 млн ₽</p>
                  <p className="text-gray-500">0% на всё первые 3 месяца</p>
                </div>

                <div className="space-y-2 mb-6">
                  {['Без комиссий и скрытых платежей', 'Одобрение за 5 минут онлайн', 'Круглосуточная поддержка 24/7'].map((text, i) => (
                    <div key={i} className="flex items-center text-sm text-gray-600">
                      <CheckCircle className="text-green-500 mr-2 flex-shrink-0" size={16} /> {text}
                    </div>
                  ))}
                </div>
              </div>

              <div className="flex-1 flex flex-col gap-3">
                <Link href="/auth/register"
                  className="w-full px-6 py-4 bg-gradient-to-r from-blue-600 to-cyan-500 text-white font-semibold rounded-xl hover:shadow-lg transition-all flex items-center justify-center group text-lg">
                  Открыть счет бесплатно
                  <ArrowRight className="ml-2 group-hover:translate-x-1 transition-transform" size={20} />
                </Link>
                <Link href="/cards"
                  className="w-full px-6 py-4 border border-gray-200 text-gray-700 font-semibold rounded-xl hover:bg-gray-50 transition-all flex items-center justify-center text-lg">
                  Оформить карту
                </Link>
                <Link href="/credits"
                  className="w-full px-6 py-4 border border-gray-200 text-gray-700 font-semibold rounded-xl hover:bg-gray-50 transition-all flex items-center justify-center text-lg">
                  Рассчитать кредит
                </Link>
              </div>
            </div>
          </motion.div>
        </div>
      </section>

      {/* Статистика — без квадратов */}
      <section className="py-16 bg-white relative z-10">
        <div className="container mx-auto px-4">
          <AnimatedStats />
        </div>
      </section>

      {/* Как начать */}
      <section className="py-16 bg-gray-50 relative z-10">
        <div className="container mx-auto px-4">
          <motion.div initial={{ opacity: 0, y: 20 }} whileInView={{ opacity: 1, y: 0 }} viewport={{ once: true }}
            className="max-w-4xl mx-auto mb-12 text-center">
            <h2 className="text-3xl font-bold mb-3">Начать пользоваться <span className="text-blue-500">просто</span></h2>
            <p className="text-gray-500">4 простых шага до полного доступа ко всем услугам банка</p>
          </motion.div>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
            {steps.map((step, index) => (
              <motion.div key={index} initial={{ opacity: 0, y: 20 }} whileInView={{ opacity: 1, y: 0 }} viewport={{ once: true }}
                transition={{ delay: index * 0.1 }} whileHover={{ y: -5 }}
                className="bg-white rounded-xl p-6 shadow-md hover:shadow-lg transition-all">
                <div className="text-4xl font-bold text-blue-500/20 mb-3">{step.number}</div>
                <h3 className="font-semibold mb-2">{step.title}</h3>
                <p className="text-gray-500 text-sm">{step.desc}</p>
              </motion.div>
            ))}
          </div>
        </div>
      </section>

      {/* Интерактивная карта */}
      <section className="py-16 bg-white relative z-10">
        <div className="container mx-auto px-4">
          <motion.div initial={{ opacity: 0, y: 20 }} whileInView={{ opacity: 1, y: 0 }} viewport={{ once: true }}
            className="max-w-4xl mx-auto mb-12 text-center">
            <h2 className="text-3xl font-bold mb-3">Ваша новая <span className="text-blue-500">карта</span></h2>
            <p className="text-gray-500">Выберите дизайн и настройте карту под себя</p>
          </motion.div>
          <InteractiveCard />
        </div>
      </section>

      {/* Преимущества */}
      <section className="py-16 bg-gray-50 relative z-10">
        <div className="container mx-auto px-4">
          <motion.div initial={{ opacity: 0 }} whileInView={{ opacity: 1 }} viewport={{ once: true }}
            className="text-center mb-12">
            <h2 className="text-3xl font-bold mb-3">Почему выбирают <span className="text-blue-500">ТБанк</span></h2>
            <p className="text-gray-500">20 лет на рынке, 2 млн клиентов и самые современные технологии</p>
          </motion.div>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {features.map((feature, index) => (
              <motion.div key={index} initial={{ opacity: 0, y: 20 }} whileInView={{ opacity: 1, y: 0 }} viewport={{ once: true }}
                transition={{ delay: index * 0.1 }} whileHover={{ y: -5 }}
                className="p-6 bg-white rounded-xl shadow-md hover:shadow-lg transition-all">
                <div className="w-12 h-12 rounded-xl bg-blue-50 flex items-center justify-center mb-4">{feature.icon}</div>
                <h3 className="font-semibold mb-2">{feature.title}</h3>
                <p className="text-gray-500 text-sm">{feature.description}</p>
              </motion.div>
            ))}
          </div>
        </div>
      </section>

      {/* Услуги */}
      <section className="py-16 bg-gradient-to-br from-blue-50 to-cyan-50 relative z-10">
        <div className="container mx-auto px-4">
          <h2 className="text-3xl font-bold text-center mb-12">Финансовые <span className="text-blue-500">услуги</span></h2>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
            {services.map((service, index) => (
              <motion.div key={index} initial={{ opacity: 0, scale: 0.95 }} whileInView={{ opacity: 1, scale: 1 }} viewport={{ once: true }}
                transition={{ delay: index * 0.1 }} whileHover={{ scale: 1.03 }}
                className={`bg-gradient-to-br ${service.color} text-white rounded-2xl p-6 shadow-xl`}>
                <h3 className="text-xl font-bold mb-3">{service.title}</h3>
                <p className="text-sm opacity-90 mb-4">{service.description}</p>
                <div className="text-3xl font-bold mb-4">{service.rate}</div>
                <div className="space-y-1 mb-4">
                  {service.features.map((f, i) => (
                    <div key={i} className="flex items-center text-sm"><CheckCircle className="mr-2" size={14} />{f}</div>
                  ))}
                </div>
                <button className="w-full py-2 bg-white/20 hover:bg-white/30 rounded-lg transition-colors text-sm font-medium">Подробнее</button>
              </motion.div>
            ))}
          </div>
        </div>
      </section>

      {/* CTA */}
      <section className="py-16 relative overflow-hidden">
        <div className="absolute inset-0 bg-gradient-to-r from-blue-600 to-cyan-500"></div>
        <div className="container mx-auto px-4 relative z-10 text-center text-white">
          <motion.h2 initial={{ opacity: 0, y: 20 }} whileInView={{ opacity: 1, y: 0 }} viewport={{ once: true }}
            className="text-3xl md:text-4xl font-bold mb-4">Готовы начать?</motion.h2>
          <p className="text-lg mb-6 opacity-90">Откройте счет онлайн за 5 минут и получите <span className="font-bold text-yellow-300">1000 ₽ на счет</span></p>
          <div className="flex gap-3 justify-center">
            <Link href="/auth/register" className="px-6 py-3 bg-white text-blue-600 font-semibold rounded-xl hover:shadow-lg transition-all">Открыть счет</Link>
            <Link href="#" className="px-6 py-3 border-2 border-white text-white font-semibold rounded-xl hover:bg-white/10 transition-all">Записаться в отделение</Link>
          </div>
        </div>
      </section>
    </div>
  )
}