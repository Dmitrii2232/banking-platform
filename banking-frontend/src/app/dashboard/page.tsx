'use client'

import { useEffect, useState, useCallback } from 'react'
import { useRouter } from 'next/navigation'
import { 
  User, CreditCard, LogOut, Plus, Wallet, PiggyBank, Banknote, 
  TrendingUp, Send, ArrowDownToLine, ArrowUpFromLine, Star,
  ChevronLeft, ChevronRight, History, Bell, Settings, HelpCircle,
  Eye, EyeOff, RefreshCw, ArrowRight
} from 'lucide-react'
import Link from 'next/link'
import { authService, UserProfile } from '@/lib/auth'
import { api, Product, productNames } from '@/lib/api'

export default function DashboardPage() {
  const router = useRouter()
  const [user, setUser] = useState<UserProfile | null>(null)
  const [products, setProducts] = useState<Product[]>([])
  const [totalBalance, setTotalBalance] = useState('0')
  const [loading, setLoading] = useState(true)
  const [showBalance, setShowBalance] = useState(true)
  const [showMenu, setShowMenu] = useState(false)
  const [isCreating, setIsCreating] = useState(false)
  const [activeCardIndex, setActiveCardIndex] = useState(0)

  // Имитация истории операций
  const [transactions] = useState([
    { id: '1', type: 'in', amount: '5000', from: 'Пополнение счета', date: 'Сегодня, 14:23' },
    { id: '2', type: 'out', amount: '1200', from: 'Оплата услуг', date: 'Вчера, 09:15' },
    { id: '3', type: 'in', amount: '15000', from: 'Перевод от Дмитрий Г.', date: '21 июля, 18:40' },
    { id: '4', type: 'out', amount: '3500', from: 'Мобильная связь', date: '20 июля, 12:00' },
    { id: '5', type: 'in', amount: '8000', from: 'Возврат покупки', date: '19 июля, 15:30' },
  ])

  useEffect(() => {
    const currentUser = authService.getCurrentUserFromStorage()
    if (!currentUser) { router.push('/auth/login'); return }
    setUser(currentUser)
    loadData()
  }, [router])

  const loadData = useCallback(async () => {
    try {
      const data = await api.getProducts()
      const sorted = [...(data.products || [])].sort((a, b) => {
        if (a.isMaster && !b.isMaster) return -1
        if (!a.isMaster && b.isMaster) return 1
        return 0
      })
      setProducts(sorted)
      const total = data.products.reduce((sum: number, p: Product) => sum + parseFloat(p.balance || '0'), 0)
      setTotalBalance(total.toLocaleString('ru-RU', { minimumFractionDigits: 2 }))
    } catch (e) {
      console.error('Ошибка загрузки данных:', e)
    } finally {
      setLoading(false)
    }
  }, [])

  const handleLogout = async () => {
    await authService.logout()
    router.push('/auth/login')
  }

  const handleOpen = async (type: string) => {
    setShowMenu(false)
    setIsCreating(true)
    try {
      switch (type) {
        case 'CURRENT_ACCOUNT':
          await api.openProduct({ productType: 'CURRENT_ACCOUNT', interestRate: '5.0', termMonths: 12 })
          break
        case 'TERM_DEPOSIT':
          await api.openTermDeposit({ interestRate: '10.0', termMonths: 12, capitalization: true, replenishable: true })
          break
        case 'CREDIT_CARD':
          await api.openCreditCard({ interestRate: '25.0', creditLimit: '100000' })
          break
        case 'LOAN':
          await api.openLoan({ amount: '500000', interestRate: '15.0', termMonths: 24 })
          break
      }
      await new Promise(resolve => setTimeout(resolve, 500))
      await loadData()
    } catch (e: any) {
      console.error('Ошибка создания продукта:', e)
    } finally {
      setIsCreating(false)
    }
  }

  const scrollCards = (direction: 'left' | 'right') => {
    if (direction === 'left') {
      setActiveCardIndex(Math.max(0, activeCardIndex - 1))
    } else {
      setActiveCardIndex(Math.min(products.length - 1, activeCardIndex + 1))
    }
  }

  const productIcons: Record<string, JSX.Element> = {
    CURRENT_ACCOUNT: <Wallet size={18} />,
    TERM_DEPOSIT: <PiggyBank size={18} />,
    CREDIT_CARD: <CreditCard size={18} />,
    LOAN: <Banknote size={18} />,
  }

  const getCardGradient = (productType: string, isMaster: boolean) => {
    if (isMaster) return 'bg-gradient-to-br from-amber-400 to-orange-500'
    switch (productType) {
      case 'CURRENT_ACCOUNT': return 'bg-gradient-to-br from-blue-500 to-cyan-500'
      case 'TERM_DEPOSIT': return 'bg-gradient-to-br from-green-500 to-emerald-500'
      case 'CREDIT_CARD': return 'bg-gradient-to-br from-purple-500 to-pink-500'
      case 'LOAN': return 'bg-gradient-to-br from-orange-500 to-red-500'
      default: return 'bg-gradient-to-br from-gray-500 to-gray-700'
    }
  }

  if (loading && products.length === 0) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-500 mx-auto"></div>
          <p className="mt-4 text-gray-600">Загрузка данных...</p>
        </div>
      </div>
    )
  }

  return (
    <div className="min-h-screen bg-gray-50 flex">
      {/* ========== ЛЕВАЯ БОКОВАЯ ПАНЕЛЬ ========== */}
      <aside className="hidden lg:flex lg:flex-col w-64 bg-white border-r border-gray-200 fixed inset-y-0 z-30">
        {/* Логотип */}
        <div className="p-5 border-b border-gray-100">
          <Link href="/" className="flex items-center space-x-2">
            <div className="w-8 h-8 bg-gradient-to-br from-blue-600 to-cyan-500 rounded-lg flex items-center justify-center">
              <CreditCard className="text-white" size={16} />
            </div>
            <span className="text-lg font-bold bg-gradient-to-r from-blue-600 to-cyan-500 bg-clip-text text-transparent">ТБанк</span>
          </Link>
        </div>

        {/* Навигация */}
        <nav className="flex-1 p-3 space-y-1 overflow-y-auto">
          <NavItem icon={<Wallet size={20} />} label="Главная" active />
          <NavItem icon={<CreditCard size={20} />} label="Карты" />
          <NavItem icon={<PiggyBank size={20} />} label="Счета" />
          <NavItem icon={<Banknote size={20} />} label="Вклады" />
          <NavItem icon={<TrendingUp size={20} />} label="Кредиты" />
          
          <div className="my-3 border-t border-gray-100"></div>
          
          <Link href="/transfers" className="flex items-center gap-3 px-3 py-2.5 rounded-lg text-gray-600 hover:bg-gray-100 transition-colors">
            <Send size={20} />
            <span className="text-sm font-medium">Переводы</span>
          </Link>
          
          <NavItem icon={<History size={20} />} label="История" />
          
          <div className="my-3 border-t border-gray-100"></div>
          
          <NavItem icon={<Bell size={20} />} label="Уведомления" />
          <NavItem icon={<Settings size={20} />} label="Настройки" />
          <NavItem icon={<HelpCircle size={20} />} label="Помощь" />
        </nav>

        {/* Профиль внизу */}
        <div className="p-4 border-t border-gray-100">
          <div className="flex items-center gap-3">
            <div className="w-9 h-9 bg-gradient-to-br from-blue-500 to-cyan-500 rounded-full flex items-center justify-center">
              <User className="text-white" size={16} />
            </div>
            <div className="flex-1 min-w-0">
              <p className="text-sm font-medium truncate">{user?.firstName} {user?.lastName}</p>
              <p className="text-xs text-gray-400 truncate">{user?.username}</p>
            </div>
            <button onClick={handleLogout} className="text-gray-400 hover:text-red-500">
              <LogOut size={18} />
            </button>
          </div>
        </div>
      </aside>

      {/* ========== ОСНОВНОЙ КОНТЕНТ ========== */}
      <div className="flex-1 lg:ml-64">
        {/* Верхняя панель (мобильная) */}
        <header className="lg:hidden bg-white shadow-sm border-b sticky top-0 z-20">
          <div className="flex justify-between items-center px-4 py-3">
            <Link href="/" className="flex items-center space-x-2">
              <div className="w-8 h-8 bg-gradient-to-br from-blue-600 to-cyan-500 rounded-lg flex items-center justify-center">
                <CreditCard className="text-white" size={16} />
              </div>
              <span className="text-lg font-bold bg-gradient-to-r from-blue-600 to-cyan-500 bg-clip-text text-transparent">ТБанк</span>
            </Link>
            <div className="flex items-center gap-2">
              <Link href="/transfers" className="p-2 text-blue-500 hover:bg-blue-50 rounded-lg">
                <Send size={20} />
              </Link>
              <button onClick={handleLogout} className="p-2 text-gray-400 hover:text-red-500">
                <LogOut size={20} />
              </button>
            </div>
          </div>
        </header>

        <main className="p-4 lg:p-6 space-y-5">
          {/* Приветствие + быстрые действия */}
          <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
            <div>
              <h1 className="text-xl lg:text-2xl font-bold text-gray-900">
                Здравствуйте, {user?.firstName}!
              </h1>
              <p className="text-sm text-gray-500 mt-0.5">ID: {user?.clientId?.substring(0, 8)}</p>
            </div>
            
            {/* Быстрые кнопки действий */}
            <div className="flex gap-2 overflow-x-auto pb-1">
              <Link href="/transfers" className="flex items-center gap-1.5 px-4 py-2 bg-blue-500 text-white text-sm font-medium rounded-lg hover:bg-blue-600 whitespace-nowrap shadow-sm">
                <Send size={15} /> Перевести
              </Link>
              <button className="flex items-center gap-1.5 px-4 py-2 bg-white border border-gray-200 text-gray-700 text-sm font-medium rounded-lg hover:bg-gray-50 whitespace-nowrap shadow-sm">
                <ArrowDownToLine size={15} /> Пополнить
              </button>
              <button className="flex items-center gap-1.5 px-4 py-2 bg-white border border-gray-200 text-gray-700 text-sm font-medium rounded-lg hover:bg-gray-50 whitespace-nowrap shadow-sm">
                <ArrowUpFromLine size={15} /> Снять
              </button>
            </div>
          </div>

          {/* ========== ГОРИЗОНТАЛЬНЫЙ СКРОЛЛ КАРТОЧЕК ========== */}
          <div className="relative">
            <div className="flex items-center gap-3 overflow-x-auto pb-2 scrollbar-hide snap-x snap-mandatory">
              {products.map((p, index) => (
                <div
                  key={p.productId}
                  className={`snap-start flex-shrink-0 w-[280px] sm:w-[320px] ${getCardGradient(p.productType, p.isMaster)} rounded-2xl p-5 text-white shadow-lg cursor-pointer transform transition-transform hover:scale-[1.02]`}
                >
                  {/* Тип продукта */}
                  <div className="flex items-center justify-between mb-6">
                    <div className="flex items-center gap-2">
                      {productIcons[p.productType]}
                      <span className="text-sm font-medium opacity-90">
                        {productNames[p.productType]}
                      </span>
                    </div>
                    {p.isMaster && (
                      <div className="flex items-center gap-1 bg-white/20 px-2 py-0.5 rounded-full text-xs">
                        <Star size={10} className="fill-white" /> Мастер
                      </div>
                    )}
                  </div>

                  {/* Баланс */}
                  <div className="mb-4">
                    <p className="text-xs opacity-70 mb-1">
                      {showBalance ? 'Текущий баланс' : 'Баланс скрыт'}
                    </p>
                    <p className="text-2xl font-bold">
                      {showBalance 
                        ? `${parseFloat(p.balance).toLocaleString()} ${p.currency}`
                        : '••••••'
                      }
                    </p>
                  </div>

                  {/* ID счета */}
                  <div className="flex items-center justify-between">
                    <p className="text-xs opacity-60 font-mono">
                      •••• {p.productId?.substring(0, 4)}
                    </p>
                    <span className={`text-xs px-2 py-0.5 rounded-full ${
                      p.status === 'ACTIVE' ? 'bg-white/20' : 'bg-red-500/30'
                    }`}>
                      {p.status === 'ACTIVE' ? 'Активен' : p.status}
                    </span>
                  </div>
                </div>
              ))}

              {/* Карточка "Открыть новый" */}
              <div className="snap-start flex-shrink-0 w-[280px] sm:w-[320px] border-2 border-dashed border-gray-300 rounded-2xl p-5 flex flex-col items-center justify-center text-gray-400 hover:border-blue-400 hover:text-blue-500 transition-colors cursor-pointer min-h-[200px]"
                   onClick={() => !isCreating && setShowMenu(true)}>
                <Plus size={40} className="mb-3" />
                <p className="text-sm font-medium">Открыть новый счет</p>
              </div>
            </div>

            {/* Стрелки скролла */}
            {products.length > 0 && (
              <>
                <button 
                  onClick={() => scrollCards('left')}
                  className="absolute left-0 top-1/2 -translate-y-1/2 -translate-x-3 w-8 h-8 bg-white rounded-full shadow-lg flex items-center justify-center text-gray-600 hover:text-gray-900 hidden sm:flex"
                >
                  <ChevronLeft size={18} />
                </button>
                <button 
                  onClick={() => scrollCards('right')}
                  className="absolute right-0 top-1/2 -translate-y-1/2 translate-x-3 w-8 h-8 bg-white rounded-full shadow-lg flex items-center justify-center text-gray-600 hover:text-gray-900 hidden sm:flex"
                >
                  <ChevronRight size={18} />
                </button>
              </>
            )}
          </div>

          {/* ========== ИНФОРМАЦИОННЫЕ БЛОКИ ========== */}
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-5">
            {/* Последние операции */}
            <div className="lg:col-span-2 bg-white rounded-2xl shadow-sm p-5">
              <div className="flex items-center justify-between mb-4">
                <h3 className="text-base font-bold text-gray-900">Последние операции</h3>
                <button className="text-sm text-blue-500 hover:text-blue-700 flex items-center gap-1">
                  Все <ArrowRight size={14} />
                </button>
              </div>
              
              <div className="space-y-3">
                {transactions.map(tx => (
                  <div key={tx.id} className="flex items-center justify-between py-2.5 border-b border-gray-50 last:border-0">
                    <div className="flex items-center gap-3">
                      <div className={`w-9 h-9 rounded-full flex items-center justify-center ${
                        tx.type === 'in' ? 'bg-green-100' : 'bg-gray-100'
                      }`}>
                        {tx.type === 'in' 
                          ? <ArrowDownToLine size={16} className="text-green-600" />
                          : <ArrowUpFromLine size={16} className="text-gray-500" />
                        }
                      </div>
                      <div>
                        <p className="text-sm font-medium text-gray-900">{tx.from}</p>
                        <p className="text-xs text-gray-400">{tx.date}</p>
                      </div>
                    </div>
                    <p className={`text-sm font-semibold ${
                      tx.type === 'in' ? 'text-green-600' : 'text-gray-700'
                    }`}>
                      {tx.type === 'in' ? '+' : '-'}{tx.amount} ₽
                    </p>
                  </div>
                ))}
              </div>
            </div>

            {/* Правая колонка */}
            <div className="space-y-5">
              {/* Общий баланс */}
              <div className="bg-gradient-to-br from-blue-500 to-cyan-500 rounded-2xl p-5 text-white shadow-lg">
                <div className="flex items-center justify-between mb-3">
                  <p className="text-sm opacity-80">Общий баланс</p>
                  <button onClick={() => setShowBalance(!showBalance)} className="opacity-70 hover:opacity-100">
                    {showBalance ? <EyeOff size={18} /> : <Eye size={18} />}
                  </button>
                </div>
                <p className="text-2xl font-bold mb-1">
                  {showBalance ? `${totalBalance} ₽` : '••••••'}
                </p>
                <p className="text-xs opacity-60">{products.length} продуктов</p>
              </div>

              {/* Статистика */}
              <div className="bg-white rounded-2xl shadow-sm p-5">
                <h4 className="text-sm font-bold text-gray-900 mb-3">Мои продукты</h4>
                <div className="space-y-2">
                  {products.slice(0, 4).map(p => (
                    <div key={p.productId} className="flex items-center justify-between text-sm">
                      <div className="flex items-center gap-2">
                        <div className={`w-2 h-2 rounded-full ${p.isMaster ? 'bg-amber-400' : 'bg-blue-400'}`}></div>
                        <span className="text-gray-600 truncate max-w-[120px]">{productNames[p.productType]}</span>
                        {p.isMaster && <span className="text-xs text-amber-500">⭐</span>}
                      </div>
                      <span className="font-medium text-gray-900">
                        {showBalance ? `${parseFloat(p.balance).toLocaleString()} ₽` : '••••'}
                      </span>
                    </div>
                  ))}
                </div>
                {products.length > 4 && (
                  <p className="text-xs text-gray-400 mt-2">+ еще {products.length - 4} продуктов</p>
                )}
              </div>

              {/* Кнопка открытия */}
              <button 
                onClick={() => setShowMenu(true)}
                disabled={isCreating}
                className="w-full flex items-center justify-center gap-2 px-4 py-3 bg-white border-2 border-dashed border-gray-300 text-gray-500 rounded-xl hover:border-blue-400 hover:text-blue-500 transition-colors text-sm font-medium"
              >
                <Plus size={18} />
                {isCreating ? 'Создание...' : 'Открыть новый продукт'}
              </button>
            </div>
          </div>
        </main>
      </div>

      {/* ========== МЕНЮ ОТКРЫТИЯ ПРОДУКТА ========== */}
      {showMenu && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50" onClick={() => setShowMenu(false)}>
          <div className="bg-white rounded-2xl shadow-2xl p-6 w-full max-w-sm mx-4" onClick={e => e.stopPropagation()}>
            <h3 className="text-lg font-bold mb-4">Открыть продукт</h3>
            <div className="space-y-2">
              {[
                { type: 'CURRENT_ACCOUNT', label: 'Текущий счёт', desc: 'Бесплатное обслуживание', icon: <Wallet size={18} /> },
                { type: 'TERM_DEPOSIT', label: 'Срочный вклад', desc: 'До 12% годовых', icon: <PiggyBank size={18} /> },
                { type: 'CREDIT_CARD', label: 'Кредитная карта', desc: 'Лимит до 100 000 ₽', icon: <CreditCard size={18} /> },
                { type: 'LOAN', label: 'Кредит', desc: 'До 5 млн ₽', icon: <Banknote size={18} /> },
              ].map(item => (
                <button
                  key={item.type}
                  onClick={() => handleOpen(item.type)}
                  disabled={isCreating}
                  className="w-full flex items-center gap-4 p-4 rounded-xl border border-gray-100 hover:bg-blue-50 hover:border-blue-200 transition-all text-left disabled:opacity-50"
                >
                  <div className="w-10 h-10 rounded-lg bg-blue-50 flex items-center justify-center text-blue-500">
                    {item.icon}
                  </div>
                  <div>
                    <p className="font-medium text-gray-900">{item.label}</p>
                    <p className="text-xs text-gray-500">{item.desc}</p>
                  </div>
                  <ArrowRight size={16} className="text-gray-300 ml-auto" />
                </button>
              ))}
            </div>
            <button onClick={() => setShowMenu(false)} className="w-full mt-3 py-2.5 text-sm text-gray-500 hover:text-gray-700">
              Отмена
            </button>
          </div>
        </div>
      )}
    </div>
  )
}

// Компонент элемента навигации
function NavItem({ icon, label, active = false }: { icon: JSX.Element; label: string; active?: boolean }) {
  return (
    <button className={`w-full flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium transition-colors ${
      active 
        ? 'bg-blue-50 text-blue-600' 
        : 'text-gray-600 hover:bg-gray-100'
    }`}>
      {icon}
      <span>{label}</span>
    </button>
  )
}