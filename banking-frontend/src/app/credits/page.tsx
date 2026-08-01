'use client'

import { motion } from 'framer-motion'

export default function CreditsPage() {
  return (
    <div className="min-h-screen bg-gray-50">
      <div className="max-w-7xl mx-auto px-4 py-8">
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          className="mb-8"
        >
          <h1 className="text-3xl font-bold text-gray-900">Кредиты</h1>
          <p className="text-gray-600">Выгодные кредиты на любые цели</p>
        </motion.div>
        
        <div className="text-center py-20">
          <div className="text-5xl mb-4">🏦</div>
          <h2 className="text-2xl font-bold mb-2">Раздел в разработке</h2>
          <p className="text-gray-600">Страница кредитов будет доступна в ближайшее время</p>
        </div>
      </div>
    </div>
  )
}