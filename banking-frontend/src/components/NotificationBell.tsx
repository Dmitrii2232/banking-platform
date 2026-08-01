'use client'

import { motion, AnimatePresence } from 'framer-motion'
import { Bell, X, Check, AlertCircle, Info, TrendingUp } from 'lucide-react'
import { useState, useEffect, useRef } from 'react'

type NotificationType = 'info' | 'success' | 'warning' | 'error' | 'transaction'

interface Notification {
  id: string
  title: string
  message: string
  type: NotificationType
  time: string
  timeAgo: string
  read: boolean
  action?: {
    label: string
    onClick: () => void
  }
}

export default function NotificationBell() {
  const [isOpen, setIsOpen] = useState(false)
  const [notifications, setNotifications] = useState<Notification[]>([
    {
      id: '1',
      title: 'Одобрен кредит',
      message: 'Ваша заявка на кредит 500 000 ₽ одобрена. Средства уже на счете.',
      type: 'success',
      time: '14:30',
      timeAgo: '5 минут назад',
      read: false,
      action: {
        label: 'Подробнее',
        onClick: () => console.log('Подробнее о кредите')
      }
    },
    {
      id: '2',
      title: 'Поступление зарплаты',
      message: 'На ваш счет поступила зарплата в размере 150 000 ₽',
      type: 'transaction',
      time: '12:15',
      timeAgo: '2 часа назад',
      read: false
    },
    {
      id: '3',
      title: 'Обновление условий вклада',
      message: 'Ставка по вашему вкладу увеличена до 8.5% годовых',
      type: 'info',
      time: '09:45',
      timeAgo: '1 день назад',
      read: true
    },
    {
      id: '4',
      title: 'Подозрительная операция',
      message: 'Обнаружена попытка входа с нового устройства',
      type: 'warning',
      time: '18:20',
      timeAgo: '3 дня назад',
      read: false,
      action: {
        label: 'Проверить',
        onClick: () => console.log('Проверить безопасность')
      }
    }
  ])

  const dropdownRef = useRef<HTMLDivElement>(null)
  const unreadCount = notifications.filter(n => !n.read).length

  // Закрытие при клике вне компонента
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target as Node)) {
        setIsOpen(false)
      }
    }

    document.addEventListener('mousedown', handleClickOutside)
    return () => document.removeEventListener('mousedown', handleClickOutside)
  }, [])

  const markAsRead = (id: string) => {
    setNotifications(prev =>
      prev.map(notification =>
        notification.id === id ? { ...notification, read: true } : notification
      )
    )
  }

  const markAllAsRead = () => {
    setNotifications(prev =>
      prev.map(notification => ({ ...notification, read: true }))
    )
  }

  const deleteNotification = (id: string) => {
    setNotifications(prev => prev.filter(notification => notification.id !== id))
  }

  const clearAllNotifications = () => {
    setNotifications([])
  }

  const getNotificationIcon = (type: NotificationType) => {
    switch (type) {
      case 'success':
        return <Check className="text-green-500" size={16} />
      case 'warning':
        return <AlertCircle className="text-yellow-500" size={16} />
      case 'error':
        return <X className="text-red-500" size={16} />
      case 'transaction':
        return <TrendingUp className="text-blue-500" size={16} />
      case 'info':
      default:
        return <Info className="text-blue-500" size={16} />
    }
  }

  return (
    <div className="relative" ref={dropdownRef}>
      <motion.button
        whileHover={{ scale: 1.1 }}
        whileTap={{ scale: 0.9 }}
        onClick={() => setIsOpen(!isOpen)}
        className="relative p-2 hover:bg-gray-100 rounded-lg transition-colors"
        aria-label="Уведомления"
      >
        <Bell size={22} className="text-gray-700" />
        
        {unreadCount > 0 && (
          <motion.div
            initial={{ scale: 0 }}
            animate={{ scale: 1 }}
            className="absolute -top-1 -right-1 min-w-5 h-5 bg-red-500 text-white text-xs rounded-full flex items-center justify-center px-1"
          >
            {unreadCount > 9 ? '9+' : unreadCount}
          </motion.div>
        )}
      </motion.button>

      <AnimatePresence>
        {isOpen && (
          <motion.div
            initial={{ opacity: 0, y: 10, scale: 0.95 }}
            animate={{ opacity: 1, y: 0, scale: 1 }}
            exit={{ opacity: 0, y: 10, scale: 0.95 }}
            className="absolute right-0 mt-2 w-80 bg-white rounded-xl shadow-2xl border border-gray-200 overflow-hidden z-50"
          >
            <div className="max-h-96 overflow-y-auto">
              <div className="sticky top-0 bg-white z-10 border-b">
                <div className="flex items-center justify-between p-4">
                  <div className="flex items-center space-x-2">
                    <h3 className="font-semibold text-gray-900">Уведомления</h3>
                    {unreadCount > 0 && (
                      <span className="px-2 py-0.5 bg-blue-100 text-blue-600 text-xs rounded-full">
                        {unreadCount} новых
                      </span>
                    )}
                  </div>
                  <div className="flex space-x-2">
                    {unreadCount > 0 && (
                      <button
                        onClick={markAllAsRead}
                        className="text-sm text-blue-500 hover:text-blue-700"
                      >
                        Прочитать все
                      </button>
                    )}
                  </div>
                </div>
              </div>

              {notifications.length > 0 ? (
                <div className="divide-y divide-gray-100">
                  {notifications.map((notification) => (
                    <div
                      key={notification.id}
                      className={`relative hover:bg-gray-50 p-4 ${!notification.read ? 'bg-blue-50/50' : ''}`}
                    >
                      {!notification.read && (
                        <div className="absolute left-3 top-4 w-2 h-2 bg-blue-500 rounded-full"></div>
                      )}

                      <div className="pl-6">
                        <div className="flex items-start justify-between">
                          <div className="flex items-start space-x-3 flex-1">
                            <div className="mt-0.5">
                              {getNotificationIcon(notification.type)}
                            </div>
                            <div className="flex-1">
                              <h4 className="font-semibold text-gray-900 text-sm">
                                {notification.title}
                              </h4>
                              <p className="text-gray-600 text-sm mt-1">
                                {notification.message}
                              </p>
                              
                              <div className="flex items-center justify-between mt-2">
                                <span className="text-xs text-gray-500">
                                  {notification.timeAgo}
                                </span>
                                
                                {notification.action && (
                                  <button
                                    onClick={notification.action.onClick}
                                    className="text-xs text-blue-500 hover:text-blue-700"
                                  >
                                    {notification.action.label}
                                  </button>
                                )}
                              </div>
                            </div>
                          </div>

                          <div className="flex space-x-1 ml-2">
                            {!notification.read && (
                              <button
                                onClick={() => markAsRead(notification.id)}
                                className="p-1 hover:bg-gray-200 rounded"
                                title="Пометить как прочитанное"
                              >
                                <Check size={14} className="text-gray-400" />
                              </button>
                            )}
                            <button
                              onClick={() => deleteNotification(notification.id)}
                              className="p-1 hover:bg-gray-200 rounded"
                              title="Удалить"
                            >
                              <X size={14} className="text-gray-400" />
                            </button>
                          </div>
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              ) : (
                <div className="p-8 text-center">
                  <div className="w-16 h-16 mx-auto mb-4 bg-gray-100 rounded-full flex items-center justify-center">
                    <Bell className="text-gray-400" size={24} />
                  </div>
                  <h4 className="font-semibold text-gray-900 mb-2">Нет уведомлений</h4>
                  <p className="text-gray-500 text-sm">
                    Здесь будут появляться важные обновления
                  </p>
                </div>
              )}
            </div>
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  )
}