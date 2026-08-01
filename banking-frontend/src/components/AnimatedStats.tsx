'use client'

import { useState } from 'react'
import CountUp from 'react-countup'
import { useInView } from 'react-intersection-observer'
import { motion } from 'framer-motion'
import { Users, CreditCard, Building, TrendingUp } from 'lucide-react'

const stats = [
  { 
    icon: <Users className="text-blue-500" size={32} />,
    value: 2000000,
    suffix: '+',
    label: 'Активных клиентов',
    description: 'По всей России'
  },
  { 
    icon: <CreditCard className="text-green-500" size={32} />,
    value: 5000000,
    suffix: '+',
    label: 'Выданных карт',
    description: 'С 1998 года'
  },
  { 
    icon: <Building className="text-purple-500" size={32} />,
    value: 250,
    suffix: '+',
    label: 'Отделений',
    description: 'В 85 регионах'
  },
  { 
    icon: <TrendingUp className="text-orange-500" size={32} />,
    value: 98.7,
    suffix: '%',
    label: 'Удовлетворенность',
    description: 'По оценкам клиентов'
  },
]

export default function AnimatedStats() {
  const [hasAnimated, setHasAnimated] = useState(false)
  const { ref, inView } = useInView({
    triggerOnce: true,
    threshold: 0.1,
  })

  return (
    <div ref={ref} className="py-12">
      <motion.div
        initial={{ opacity: 0, y: 30 }}
        whileInView={{ opacity: 1, y: 0 }}
        viewport={{ once: true }}
        transition={{ duration: 0.6 }}
        className="max-w-4xl mx-auto text-center mb-16"
      >
        <h2 className="text-4xl font-bold mb-4">
          ТБанк в <span className="gradient-text">цифрах</span>
        </h2>
        <p className="text-gray-600 text-lg">
          20 лет лидерства на финансовом рынке России
        </p>
      </motion.div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-8">
        {stats.map((stat, index) => (
          <motion.div
            key={index}
            initial={{ opacity: 0, y: 50 }}
            whileInView={{ opacity: 1, y: 0 }}
            viewport={{ once: true }}
            transition={{ duration: 0.5, delay: index * 0.1 }}
            whileHover={{ y: -10 }}
            className="bg-white rounded-2xl p-8 shadow-lg hover:shadow-2xl transition-all duration-300"
          >
            <div className="flex justify-center mb-6">
              <div className="w-16 h-16 rounded-xl bg-gray-50 flex items-center justify-center">
                {stat.icon}
              </div>
            </div>
            
            <div className="text-center mb-2">
              <div className="text-4xl font-bold text-gray-900 mb-1">
                {inView ? (
                  <CountUp
                    start={0}
                    end={stat.value}
                    duration={2.5}
                    suffix={stat.suffix}
                    decimals={stat.value % 1 !== 0 ? 1 : 0}
                  />
                ) : (
                  '0' + stat.suffix
                )}
              </div>
              <div className="text-lg font-semibold text-gray-800">{stat.label}</div>
            </div>
            
            <div className="text-center">
              <p className="text-gray-600 text-sm">{stat.description}</p>
            </div>
            
            {/* Анимированный прогресс бар */}
            <div className="mt-6">
              <div className="h-2 bg-gray-200 rounded-full overflow-hidden">
                <motion.div
                  initial={{ width: 0 }}
                  whileInView={{ width: '100%' }}
                  viewport={{ once: true }}
                  transition={{ duration: 2, delay: 0.5 }}
                  className="h-full bg-gradient-to-r from-blue-500 to-cyan-500"
                />
              </div>
            </div>
          </motion.div>
        ))}
      </div>

      {/* Дополнительная статистика */}
      <motion.div
        initial={{ opacity: 0 }}
        whileInView={{ opacity: 1 }}
        viewport={{ once: true }}
        transition={{ delay: 0.5 }}
        className="mt-16 bg-gradient-to-r from-blue-50 to-cyan-50 rounded-2xl p-8"
      >
        <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
          <div className="text-center">
            <div className="text-2xl font-bold text-blue-600">24/7</div>
            <div className="text-gray-700">Работаем без выходных</div>
          </div>
          <div className="text-center">
            <div className="text-2xl font-bold text-green-600">99.9%</div>
            <div className="text-gray-700">Доступность сервиса</div>
          </div>
          <div className="text-center">
            <div className="text-2xl font-bold text-purple-600">5 мин</div>
            <div className="text-gray-700">Среднее время ответа поддержки</div>
          </div>
        </div>
      </motion.div>
    </div>
  )
}