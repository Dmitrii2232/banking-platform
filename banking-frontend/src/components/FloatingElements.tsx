'use client'

import { motion } from 'framer-motion'
import { CreditCard, TrendingUp, Shield, Globe, DollarSign, PieChart } from 'lucide-react'

export default function FloatingElements() {
  const elements = [
    {
      icon: <CreditCard className="text-blue-500" size={20} />,
      top: '10%',
      left: '5%',
      delay: 0,
      size: 'w-12 h-12'
    },
    {
      icon: <TrendingUp className="text-green-500" size={20} />,
      top: '20%',
      right: '10%',
      delay: 0.3,
      size: 'w-14 h-14'
    },
    {
      icon: <Shield className="text-purple-500" size={20} />,
      bottom: '30%',
      left: '7%',
      delay: 0.6,
      size: 'w-10 h-10'
    },
    {
      icon: <Globe className="text-orange-500" size={20} />,
      bottom: '20%',
      right: '5%',
      delay: 0.9,
      size: 'w-16 h-16'
    },
    {
      icon: <DollarSign className="text-cyan-500" size={20} />,
      top: '40%',
      left: '15%',
      delay: 1.2,
      size: 'w-8 h-8'
    },
    {
      icon: <PieChart className="text-pink-500" size={20} />,
      top: '60%',
      right: '15%',
      delay: 1.5,
      size: 'w-12 h-12'
    },
  ]

  return (
    <div className="fixed inset-0 overflow-hidden pointer-events-none" style={{ zIndex: 1 }}>
      {elements.map((element, index) => (
        <motion.div
          key={index}
          className={`absolute ${element.size} bg-white/30 backdrop-blur-sm rounded-2xl flex items-center justify-center shadow-lg border border-white/20`}
          style={{
            top: element.top,
            left: element.left,
            right: element.right,
            bottom: element.bottom,
          }}
          initial={{ y: 0, opacity: 0, rotate: 0 }}
          animate={{
            y: [0, -30, 0],
            opacity: [0.4, 0.9, 0.4],
            rotate: [0, 180, 360],
          }}
          transition={{
            duration: 6 + index,
            delay: element.delay,
            repeat: Infinity,
            ease: "easeInOut",
          }}
        >
          {element.icon}
        </motion.div>
      ))}
      
      {/* Плавающие точки */}
      {[...Array(20)].map((_, i) => (
        <motion.div
          key={`dot-${i}`}
          className="absolute w-1 h-1 bg-blue-400/40 rounded-full"
          style={{
            top: `${Math.random() * 100}%`,
            left: `${Math.random() * 100}%`,
          }}
          animate={{
            y: [0, -20, 0],
            opacity: [0.2, 0.7, 0.2],
          }}
          transition={{
            duration: 3 + Math.random() * 4,
            repeat: Infinity,
            delay: Math.random() * 2,
          }}
        />
      ))}
    </div>
  )
}