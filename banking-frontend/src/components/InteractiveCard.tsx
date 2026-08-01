'use client'

import { useState } from 'react'
import { motion } from 'framer-motion'
import { CreditCard, Eye, EyeOff, Smartphone, Wifi, Zap, Shield, Gift, RotateCw } from 'lucide-react'

const cardDesigns = [
  { id: 1, name: 'Классический', gradient: 'from-blue-500 to-cyan-500', textColor: 'text-white', price: 'Бесплатно' },
  { id: 2, name: 'Премиум', gradient: 'from-purple-500 to-pink-500', textColor: 'text-white', price: '500 ₽/мес' },
  { id: 3, name: 'Черный', gradient: 'from-gray-900 to-gray-700', textColor: 'text-white', price: '300 ₽/мес' },
  { id: 4, name: 'Золотой', gradient: 'from-yellow-600 to-yellow-400', textColor: 'text-gray-900', price: '1000 ₽/мес' },
  { id: 5, name: 'Градиент', gradient: 'from-green-500 to-blue-500', textColor: 'text-white', price: '200 ₽/мес' },
]

const cardBenefits = [
  { icon: <Zap size={18} />, text: 'Кэшбэк до 10%' },
  { icon: <Shield size={18} />, text: 'Страхование покупок' },
  { icon: <Gift size={18} />, text: 'Приветственные бонусы' },
  { icon: <Smartphone size={18} />, text: 'Бесконтактная оплата' },
]

export default function InteractiveCard() {
  const [selectedDesign, setSelectedDesign] = useState(cardDesigns[0])
  const [cardNumber, setCardNumber] = useState('1234567890123456')
  const [showNumber, setShowNumber] = useState(false)
  const [cardHolder, setCardHolder] = useState('ИВАН ИВАНОВ')
  const [expiryDate, setExpiryDate] = useState('12/28')
  const [isFlipped, setIsFlipped] = useState(false)
  const [balance, setBalance] = useState(125000)
  const [cvv, setCvv] = useState('123')

  const formatCardNumber = (num: string) => {
    return num.match(/.{1,4}/g)?.join(' ') || ''
  }

  const handleDeposit = () => {
    setBalance(prev => prev + 10000)
  }

  const handleWithdraw = () => {
    if (balance >= 10000) {
      setBalance(prev => prev - 10000)
    }
  }

  return (
    <div className="max-w-6xl mx-auto">
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-12 items-center">
        {/* Карта */}
        <div className="relative perspective-1000">
          <motion.div
            animate={{ rotateY: isFlipped ? 180 : 0 }}
            transition={{ duration: 0.6 }}
            className="relative w-full"
            style={{ transformStyle: 'preserve-3d' }}
          >
            {/* Лицевая сторона */}
            <div
              className={`bg-gradient-to-br ${selectedDesign.gradient} rounded-3xl p-8 shadow-2xl cursor-pointer w-full`}
              style={{ backfaceVisibility: 'hidden' }}
              onClick={() => setIsFlipped(!isFlipped)}
            >
              <div className="flex justify-between items-start mb-12">
                <div>
                  <h3 className="text-2xl font-bold mb-1">ТБанк</h3>
                  <p className="opacity-90">World Elite</p>
                </div>
                <div className="flex items-center space-x-3">
                  <div className="w-12 h-12 bg-white/20 rounded-xl flex items-center justify-center">
                    <Wifi className="text-white" size={24} />
                  </div>
                  <Smartphone className="text-white/80" size={32} />
                </div>
              </div>

              <div className="mb-8">
                <div className="flex items-center justify-between mb-4">
                  <div className={`text-3xl font-mono tracking-widest ${selectedDesign.textColor}`}>
                    {showNumber ? formatCardNumber(cardNumber) : '•••• •••• •••• ••••'}
                  </div>
                  <button
                    onClick={(e) => {
                      e.stopPropagation()
                      setShowNumber(!showNumber)
                    }}
                    className="p-2 hover:bg-white/20 rounded-lg transition-colors"
                  >
                    {showNumber ? <EyeOff size={20} className={selectedDesign.textColor} /> : <Eye size={20} className={selectedDesign.textColor} />}
                  </button>
                </div>

                <div className="flex justify-between items-end">
                  <div>
                    <p className="text-sm opacity-80 mb-1">Владелец карты</p>
                    <p className={`text-xl font-semibold ${selectedDesign.textColor}`}>{cardHolder}</p>
                  </div>
                  <div>
                    <p className="text-sm opacity-80 mb-1">Срок действия</p>
                    <p className={`text-xl font-semibold ${selectedDesign.textColor}`}>{expiryDate}</p>
                  </div>
                  <div className="w-16 h-16 bg-white/20 rounded-xl flex items-center justify-center">
                    <CreditCard className="text-white" size={32} />
                  </div>
                </div>
              </div>

              <div className="flex justify-between items-center pt-6 border-t border-white/30">
                <div>
                  <p className="text-sm opacity-80">Баланс</p>
                  <p className="text-3xl font-bold">{balance.toLocaleString('ru-RU')} ₽</p>
                </div>
                <div className="flex space-x-2">
                  <div className="w-12 h-8 bg-white rounded"></div>
                  <div className="w-12 h-8 bg-red-500 rounded"></div>
                </div>
              </div>
            </div>

            {/* Обратная сторона */}
            <div
              className={`absolute inset-0 bg-gradient-to-br ${selectedDesign.gradient} rounded-3xl p-8 shadow-2xl cursor-pointer`}
              style={{ 
                backfaceVisibility: 'hidden',
                transform: 'rotateY(180deg)'
              }}
              onClick={() => setIsFlipped(!isFlipped)}
            >
              <div className="h-12 bg-black mt-8 mb-6"></div>
              <div className="flex justify-end mb-6">
                <div className="w-16 h-10 bg-white/20 rounded-lg flex items-center justify-center">
                  <span className="text-white font-mono">{cvv}</span>
                </div>
              </div>
              <div className="text-center mt-12">
                <p className="text-sm opacity-80">Для активации обратитесь в отделение банка</p>
                <p className="text-xs opacity-60 mt-2">+7 (800) 555-35-35</p>
              </div>
            </div>
          </motion.div>

          <button
            onClick={() => setIsFlipped(!isFlipped)}
            className="absolute -bottom-4 left-1/2 -translate-x-1/2 w-12 h-12 bg-white rounded-full shadow-lg flex items-center justify-center hover:scale-110 transition-transform"
          >
            <RotateCw size={20} />
          </button>
        </div>

        {/* Настройки карты */}
        <div className="space-y-8">
          <div>
            <h3 className="text-2xl font-bold mb-6">Выберите дизайн карты</h3>
            <div className="grid grid-cols-2 md:grid-cols-3 gap-4">
              {cardDesigns.map((design) => (
                <button
                  key={design.id}
                  onClick={() => setSelectedDesign(design)}
                  className={`relative p-4 rounded-xl border-2 transition-all ${
                    selectedDesign.id === design.id 
                      ? 'border-blue-500 ring-2 ring-blue-200' 
                      : 'border-gray-200 hover:border-gray-300'
                  }`}
                >
                  <div className={`h-24 rounded-lg ${design.gradient} mb-3`}></div>
                  <div className="text-center">
                    <p className="font-medium">{design.name}</p>
                    <p className="text-sm text-gray-600">{design.price}</p>
                  </div>
                  {selectedDesign.id === design.id && (
                    <div className="absolute -top-2 -right-2 w-6 h-6 bg-blue-500 rounded-full flex items-center justify-center">
                      <div className="w-2 h-2 bg-white rounded-full"></div>
                    </div>
                  )}
                </button>
              ))}
            </div>
          </div>

          {/* Преимущества */}
          <div>
            <h3 className="text-xl font-semibold mb-4">Преимущества карты</h3>
            <div className="grid grid-cols-2 gap-3">
              {cardBenefits.map((benefit, index) => (
                <div key={index} className="flex items-center space-x-3 p-3 bg-gray-50 rounded-lg">
                  <div className="text-blue-500">
                    {benefit.icon}
                  </div>
                  <span className="text-sm">{benefit.text}</span>
                </div>
              ))}
            </div>
          </div>

          {/* Управление балансом */}
          <div className="bg-gray-50 rounded-2xl p-6">
            <h3 className="text-xl font-semibold mb-4">Управление балансом</h3>
            <div className="space-y-4">
              <div className="flex items-center justify-between">
                <span>Текущий баланс</span>
                <span className="text-2xl font-bold">{balance.toLocaleString('ru-RU')} ₽</span>
              </div>
              <div className="flex space-x-4">
                <button
                  onClick={handleDeposit}
                  className="flex-1 py-3 bg-green-500 text-white rounded-lg hover:bg-green-600 transition-colors font-medium"
                >
                  + Пополнить
                </button>
                <button
                  onClick={handleWithdraw}
                  className="flex-1 py-3 bg-red-500 text-white rounded-lg hover:bg-red-600 transition-colors font-medium"
                >
                  - Снять
                </button>
              </div>
            </div>
          </div>

          <button className="w-full py-4 bg-gradient-to-r from-blue-500 to-cyan-500 text-white rounded-xl font-semibold hover:shadow-lg transition-all duration-300">
            Оформить карту
          </button>
        </div>
      </div>
    </div>
  )
}