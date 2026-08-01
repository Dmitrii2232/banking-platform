'use client'

import { useState, useEffect } from 'react'
import { motion, AnimatePresence } from 'framer-motion'
import { ChevronLeft, ChevronRight, Pause, Play } from 'lucide-react'

const banners = [
  {
    id: 1,
    title: 'Кредитная карта',
    subtitle: '0% на всё первые 3 месяца',
    description: 'Кредитный лимит до 1 млн ₽',
    buttonText: 'Оформить',
    gradient: 'from-slate-800 via-slate-700 to-slate-900',
    features: ['Кэшбэк до 10%', 'Бесплатное обслуживание'],
    icon: '💳'
  },
  {
    id: 2,
    title: 'Выгодный вклад',
    subtitle: 'До 12% годовых',
    description: 'С капитализацией процентов',
    buttonText: 'Открыть вклад',
    gradient: 'from-emerald-800 via-teal-700 to-emerald-900',
    features: ['Пополнение', 'Снятие без потерь'],
    icon: '💰'
  },
  {
    id: 3,
    title: 'Ипотека 5.9%',
    subtitle: 'Одобрение за 30 минут',
    description: 'На новостройки и вторичное жильё',
    buttonText: 'Рассчитать',
    gradient: 'from-stone-700 via-stone-600 to-stone-800',
    features: ['Первый взнос 15%', 'Срок до 30 лет'],
    icon: '🏠'
  },
  {
    id: 4,
    title: 'Инвестиции',
    subtitle: 'Доходность до 25%',
    description: 'Доверительное управление',
    buttonText: 'Инвестировать',
    gradient: 'from-indigo-800 via-blue-700 to-indigo-900',
    features: ['От 10 000 ₽', 'Личный брокер'],
    icon: '📈'
  },
]

export default function BannerCarousel() {
  const [currentIndex, setCurrentIndex] = useState(0)
  const [isPlaying, setIsPlaying] = useState(true)
  const [direction, setDirection] = useState(0)
  const [progress, setProgress] = useState(0)

  useEffect(() => {
    if (!isPlaying) return
    const duration = 5000
    const interval = setInterval(() => {
      setProgress(prev => {
        if (prev >= 100) {
          setDirection(1)
          setCurrentIndex(prev => (prev + 1) % banners.length)
          return 0
        }
        return prev + (100 / (duration / 100))
      })
    }, 100)
    return () => clearInterval(interval)
  }, [isPlaying, currentIndex])

  useEffect(() => {
    setProgress(0)
  }, [currentIndex])

  const goToSlide = (index: number) => {
    setDirection(index > currentIndex ? 1 : -1)
    setCurrentIndex(index)
  }

  const variants = {
    enter: (dir: number) => ({ x: dir > 0 ? '100%' : '-100%', opacity: 0, scale: 1.05 }),
    center: { x: 0, opacity: 1, scale: 1 },
    exit: (dir: number) => ({ x: dir < 0 ? '100%' : '-100%', opacity: 0, scale: 0.95 })
  }

  return (
    <div className="relative w-full h-[80vh] overflow-hidden bg-white/10">
      {/* Фон с градиентом */}
      <AnimatePresence initial={false} custom={direction} mode="wait">
        <motion.div
          key={currentIndex}
          custom={direction}
          variants={variants}
          initial="enter"
          animate="center"
          exit="exit"
          transition={{ duration: 0.6, ease: [0.25, 0.46, 0.45, 0.94] }}
          className={`absolute inset-0 bg-gradient-to-br ${banners[currentIndex].gradient}`}
        >
          {/* Декоративные элементы */}
          <div className="absolute inset-0 opacity-30">
            <div className="absolute top-20 left-10 w-72 h-72 bg-white/10 rounded-full blur-3xl" />
            <div className="absolute bottom-20 right-10 w-96 h-96 bg-white/5 rounded-full blur-3xl" />
            <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[600px] h-[600px] border border-white/10 rounded-full" />
            <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[400px] h-[400px] border border-white/5 rounded-full" />
          </div>

          {/* Точки на фоне */}
          <div className="absolute inset-0" style={{ backgroundImage: 'radial-gradient(circle, rgba(255,255,255,0.1) 1px, transparent 1px)', backgroundSize: '40px 40px' }} />

          {/* Контент */}
          <div className="relative h-full flex items-center">
            <div className="container mx-auto px-6 lg:px-12">
              <div className="max-w-xl mx-auto text-center">
                {/* Иконка */}
                <motion.div
                  initial={{ opacity: 0, scale: 0 }}
                  animate={{ opacity: 1, scale: 1 }}
                  transition={{ delay: 0.2, duration: 0.5, type: 'spring' }}
                  className="text-6xl mb-6"
                >
                  {banners[currentIndex].icon}
                </motion.div>

                {/* Подзаголовок */}
                <motion.p
                  initial={{ opacity: 0, y: 10 }}
                  animate={{ opacity: 1, y: 0 }}
                  transition={{ delay: 0.3 }}
                  className="text-white/80 text-lg font-medium mb-3 tracking-wide uppercase"
                >
                  {banners[currentIndex].subtitle}
                </motion.p>

                {/* Заголовок */}
                <motion.h2
                  initial={{ opacity: 0, y: 20 }}
                  animate={{ opacity: 1, y: 0 }}
                  transition={{ delay: 0.4 }}
                  className="text-5xl md:text-6xl lg:text-7xl font-bold text-white mb-4 leading-tight"
                >
                  {banners[currentIndex].title}
                </motion.h2>

                {/* Описание */}
                <motion.p
                  initial={{ opacity: 0, y: 20 }}
                  animate={{ opacity: 1, y: 0 }}
                  transition={{ delay: 0.5 }}
                  className="text-xl text-white/70 mb-8"
                >
                  {banners[currentIndex].description}
                </motion.p>

                {/* Фичи */}
                <motion.div
                  initial={{ opacity: 0, y: 20 }}
                  animate={{ opacity: 1, y: 0 }}
                  transition={{ delay: 0.6 }}
                  className="flex gap-6 mb-8"
                >
                  {banners[currentIndex].features.map((feature, i) => (
                    <div key={i} className="flex items-center gap-2">
                      <div className="w-2 h-2 rounded-full bg-white/60" />
                      <span className="text-white/80 text-sm">{feature}</span>
                    </div>
                  ))}
                </motion.div>

                {/* Кнопка */}
                <motion.button
                  initial={{ opacity: 0, y: 20 }}
                  animate={{ opacity: 1, y: 0 }}
                  transition={{ delay: 0.7 }}
                  whileHover={{ scale: 1.03 }}
                  whileTap={{ scale: 0.97 }}
                  className="px-8 py-4 bg-white text-gray-900 font-semibold rounded-2xl shadow-2xl hover:shadow-white/20 transition-all"
                >
                  {banners[currentIndex].buttonText}
                </motion.button>
              </div>
            </div>
          </div>
        </motion.div>
      </AnimatePresence>

      {/* Навигация */}
      <div className="absolute bottom-8 left-1/2 -translate-x-1/2 flex items-center gap-6 z-20">
        <button onClick={() => { setDirection(-1); setCurrentIndex(prev => (prev - 1 + banners.length) % banners.length) }}
          className="w-10 h-10 rounded-full bg-white/10 hover:bg-white/20 flex items-center justify-center transition-colors">
          <ChevronLeft className="text-white" size={20} />
        </button>

        {/* Индикаторы с прогрессом */}
        <div className="flex gap-3">
          {banners.map((_, index) => (
            <button key={index} onClick={() => goToSlide(index)}
              className="relative w-12 h-1 rounded-full bg-white/20 overflow-hidden transition-all hover:bg-white/30">
              {index === currentIndex && (
                <motion.div
                  className="absolute inset-0 bg-white rounded-full"
                  initial={{ width: '0%' }}
                  animate={{ width: `${progress}%` }}
                  transition={{ duration: 0.1 }}
                />
              )}
              {index !== currentIndex && (
                <div className="absolute inset-0 bg-white/20 rounded-full" />
              )}
            </button>
          ))}
        </div>

        <button onClick={() => { setDirection(1); setCurrentIndex(prev => (prev + 1) % banners.length) }}
          className="w-10 h-10 rounded-full bg-white/10 hover:bg-white/20 flex items-center justify-center transition-colors">
          <ChevronRight className="text-white" size={20} />
        </button>
      </div>

      {/* Пауза */}
      <button onClick={() => setIsPlaying(!isPlaying)}
        className="absolute top-8 right-8 w-10 h-10 rounded-full bg-white/10 hover:bg-white/20 flex items-center justify-center transition-colors z-20">
        {isPlaying ? <Pause className="text-white" size={16} /> : <Play className="text-white" size={16} />}
      </button>

      {/* Счётчик */}
      <div className="absolute top-8 left-8 bg-white/10 backdrop-blur-sm rounded-full px-4 py-2 text-white text-sm font-medium z-20">
        {String(currentIndex + 1).padStart(2, '0')} / {String(banners.length).padStart(2, '0')}
      </div>
    </div>
  )
}