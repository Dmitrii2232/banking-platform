// src/app/transfers/page.tsx
'use client'

import { useState, useEffect } from 'react'
import { useRouter } from 'next/navigation'
import { api, Product, productNames } from '@/lib/api'
import { authService } from '@/lib/auth'
import { ArrowRight, CheckCircle, ArrowLeft, CreditCard, Phone, Search } from 'lucide-react'
import Link from 'next/link'

export default function TransfersPage() {
  const router = useRouter()
  const [products, setProducts] = useState<Product[]>([])
  const [step, setStep] = useState<'form' | 'success'>('form')
  const [transferType, setTransferType] = useState<'phone' | 'account'>('phone')
  
  // По номеру телефона
  const [phone, setPhone] = useState('')
  const [searchLoading, setSearchLoading] = useState(false)  // ← ДОБАВЛЕНО
  const [searchResult, setSearchResult] = useState<any>(null)  // ← ДОБАВЛЕНО
  const [selectedDestProduct, setSelectedDestProduct] = useState<Product | null>(null)
  
  // По счёту
  const [destId, setDestId] = useState('')
  
  // Общее
  const [sourceId, setSourceId] = useState('')
  const [amount, setAmount] = useState('')
  const [result, setResult] = useState<any>(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  useEffect(() => {
    if (!authService.isAuthenticated()) { router.push('/auth/login'); return }
    loadProducts()
  }, [router])

  const loadProducts = async () => {
    try {
      const data = await api.getProducts()
      setProducts(data.products || [])
    } catch (e) {
      console.error('Ошибка загрузки продуктов:', e)
    }
  }

  // Поиск клиента по телефону
  const searchByPhone = async () => {
    const cleanPhone = phone.replace(/[\s\(\)-]/g, '')
    
    if (cleanPhone.length < 11) {
      setError('Введите полный номер телефона')
      return
    }

    setSearchLoading(true)
    setError('')
    setSelectedDestProduct(null)
    setSearchResult(null)

    try {
      const result = await api.findClientByPhone(cleanPhone)
      setSearchResult(result)

      if (result.found && result.masterProductId) {
        // Загружаем информацию о мастер-счете получателя
        const masterAccount = await api.getProduct(result.masterProductId)
        setSelectedDestProduct(masterAccount)
      }
    } catch (e: any) {
      setError(e.message || 'Клиент не найден')
    } finally {
      setSearchLoading(false)
    }
  }

  const handleTransfer = async () => {
    setError('')
    if (!sourceId || !amount) {
      setError('Заполните все поля')
      return
    }

    let destinationId = selectedDestProduct?.productId || destId

    if (!destinationId) {
      setError(transferType === 'phone' ? 'Сначала найдите получателя по номеру телефона' : 'Введите ID счёта получателя')
      return
    }
    
    if (sourceId === destinationId) {
      setError('Нельзя перевести на тот же счёт')
      return
    }

    const sourceProduct = products.find(p => p.productId === sourceId)
    if (sourceProduct && parseFloat(sourceProduct.balance) < parseFloat(amount)) {
      setError('Недостаточно средств на счете')
      return
    }

    setLoading(true)
    try {
      const res = await api.transfer({
        sourceProductId: sourceId,
        destinationProductId: destinationId,
        amount: parseFloat(amount),
        currency: 'RUB'
      })
      setResult(res)
      setStep('success')
    } catch (e: any) {
      setError(e.message || 'Ошибка при переводе')
    } finally {
      setLoading(false)
    }
  }

  const resetForm = () => {
    setStep('form')
    setResult(null)
    setSourceId('')
    setDestId('')
    setPhone('')
    setSearchResult(null)
    setSelectedDestProduct(null)
    setAmount('')
    setError('')
  }

  const formatPhone = (value: string): string => {
    const digits = value.replace(/\D/g, '')
    if (digits.length === 0) return ''
    if (digits.length <= 1) return '+7'
    if (digits.length <= 4) return `+7 (${digits.slice(1)}`
    if (digits.length <= 7) return `+7 (${digits.slice(1, 4)}) ${digits.slice(4)}`
    if (digits.length <= 9) return `+7 (${digits.slice(1, 4)}) ${digits.slice(4, 7)}-${digits.slice(7)}`
    return `+7 (${digits.slice(1, 4)}) ${digits.slice(4, 7)}-${digits.slice(7, 9)}-${digits.slice(9, 11)}`
  }

  const sourceProduct = products.find(p => p.productId === sourceId)

  if (step === 'success' && result) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center p-6">
        <div className="max-w-md w-full bg-white rounded-2xl shadow-xl p-8 text-center">
          <div className="w-16 h-16 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-4">
            <CheckCircle className="text-green-500" size={32} />
          </div>
          <h2 className="text-2xl font-bold mb-2">Перевод выполнен!</h2>
          <p className="text-gray-500 mb-2">
            Сумма: <span className="font-bold text-lg">{parseFloat(amount).toLocaleString()} ₽</span>
          </p>
          {selectedDestProduct && (
            <p className="text-gray-500 mb-4">
              Получатель: {productNames[selectedDestProduct.productType] || 'Счет'} 
              {' '}{selectedDestProduct.productId?.substring(0, 8)}
            </p>
          )}
          <p className="text-xs text-gray-400 mb-1">ID транзакции</p>
          <p className="font-mono text-xs bg-gray-50 rounded-lg p-2 mb-6">{result.transactionId}</p>
          <div className="flex gap-3">
            <button onClick={resetForm}
              className="flex-1 px-4 py-2 border border-gray-200 rounded-lg text-gray-700 hover:bg-gray-50">
              Ещё перевод
            </button>
            <Link href="/dashboard"
              className="flex-1 px-4 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600 text-center">
              На дашборд
            </Link>
          </div>
        </div>
      </div>
    )
  }

  return (
    <div className="min-h-screen bg-gray-50 p-6 pt-24">
      <div className="max-w-lg mx-auto">
        {/* Header */}
        <div className="flex items-center justify-between mb-6">
          <Link href="/dashboard" className="flex items-center text-gray-500 hover:text-gray-700">
            <ArrowLeft size={20} className="mr-1" />
            <span className="text-sm">Назад</span>
          </Link>
          <h1 className="text-xl font-bold">Переводы</h1>
          <div className="w-16" />
        </div>

        {/* Выбор типа перевода */}
        <div className="flex bg-white rounded-xl p-1 mb-4 shadow-sm">
          <button 
            onClick={() => { setTransferType('phone'); setError(''); setDestId(''); }}
            className={`flex-1 flex items-center justify-center gap-2 py-2.5 rounded-lg text-sm font-medium transition-all ${
              transferType === 'phone' ? 'bg-blue-500 text-white shadow' : 'text-gray-500 hover:bg-gray-50'
            }`}>
            <Phone size={16} /> По телефону
          </button>
          <button 
            onClick={() => { setTransferType('account'); setError(''); setPhone(''); setSearchResult(null); setSelectedDestProduct(null); }}
            className={`flex-1 flex items-center justify-center gap-2 py-2.5 rounded-lg text-sm font-medium transition-all ${
              transferType === 'account' ? 'bg-blue-500 text-white shadow' : 'text-gray-500 hover:bg-gray-50'
            }`}>
            <CreditCard size={16} /> По счёту
          </button>
        </div>

        <div className="bg-white rounded-2xl shadow-sm p-6">
          {error && (
            <div className="mb-4 p-3 bg-red-50 border border-red-200 rounded-lg text-red-600 text-sm">{error}</div>
          )}

          {/* Отправитель */}
          <div className="mb-4">
            <label className="text-xs text-gray-500 font-medium mb-2 block">СО СЧЁТА</label>
            <select value={sourceId} onChange={e => setSourceId(e.target.value)}
              className="w-full border border-gray-200 rounded-xl p-3 text-sm focus:ring-2 focus:ring-blue-500 focus:border-transparent">
              <option value="">Выберите счёт</option>
              {products.map(p => (
                <option key={p.productId} value={p.productId}>
                  {productNames[p.productType] || p.productType} • {parseFloat(p.balance).toLocaleString()} ₽ {p.isMaster ? '(Мастер)' : ''}
                </option>
              ))}
            </select>
          </div>

          {/* Стрелка */}
          <div className="flex justify-center my-3">
            <div className="w-10 h-10 bg-blue-50 rounded-full flex items-center justify-center">
              <ArrowRight className="text-blue-500" size={20} />
            </div>
          </div>

          {/* Получатель — по телефону */}
          {transferType === 'phone' && (
            <div className="mb-4">
              <label className="text-xs text-gray-500 font-medium mb-2 block">ПОЛУЧАТЕЛЬ</label>
              <div className="flex gap-2">
                <div className="relative flex-1">
                  <Phone className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" size={18} />
                  <input 
                    type="tel" 
                    value={phone} 
                    onChange={e => setPhone(formatPhone(e.target.value))}
                    placeholder="+7 (999) 999-99-99"
                    className="w-full pl-10 pr-4 py-3 border border-gray-200 rounded-xl text-sm focus:ring-2 focus:ring-blue-500" 
                  />
                </div>
                <button 
                  onClick={searchByPhone} 
                  disabled={searchLoading}
                  className="px-4 py-3 bg-blue-500 text-white rounded-xl hover:bg-blue-600 disabled:opacity-50">
                  {searchLoading ? '...' : <Search size={18} />}
                </button>
              </div>

              {/* Результат поиска */}
              {selectedDestProduct && (
                <div className="mt-2 p-3 bg-green-50 border border-green-200 rounded-lg">
                  <p className="text-sm font-medium text-green-700">
                    Получатель найден: {productNames[selectedDestProduct.productType] || 'Счет'}
                  </p>
                  <p className="text-xs text-green-600">
                    ID: {selectedDestProduct.productId?.substring(0, 8)}
                  </p>
                </div>
              )}
              
              {searchResult && !searchResult.found && (
                <div className="mt-2 p-3 bg-red-50 border border-red-200 rounded-lg">
                  <p className="text-sm text-red-600">Клиент с таким номером не найден</p>
                </div>
              )}
            </div>
          )}

          {/* Получатель — по счёту */}
          {transferType === 'account' && (
            <div className="mb-4">
              <label className="text-xs text-gray-500 font-medium mb-2 block">НА СЧЁТ</label>
              <input 
                type="text" 
                value={destId} 
                onChange={e => setDestId(e.target.value)}
                placeholder="Введите ID продукта получателя"
                className="w-full border border-gray-200 rounded-xl p-3 text-sm focus:ring-2 focus:ring-blue-500" 
              />
            </div>
          )}

          {/* Сумма */}
          <div className="mb-6">
            <label className="text-xs text-gray-500 font-medium mb-2 block">СУММА</label>
            <div className="relative">
              <input 
                type="number" 
                value={amount} 
                onChange={e => setAmount(e.target.value)}
                placeholder="0"
                min="0"
                step="0.01"
                className="w-full border border-gray-200 rounded-xl p-3 text-2xl font-bold text-center focus:ring-2 focus:ring-blue-500" 
              />
              <span className="absolute right-4 top-1/2 -translate-y-1/2 text-gray-400 font-medium">₽</span>
            </div>
            {sourceProduct && amount && (
              <p className="text-xs text-gray-400 mt-1 text-center">
                Останется: {(parseFloat(sourceProduct.balance) - parseFloat(amount)).toLocaleString()} ₽
              </p>
            )}
          </div>

          <button 
            onClick={handleTransfer} 
            disabled={loading || !sourceId || !amount}
            className="w-full py-3 bg-gradient-to-r from-blue-500 to-cyan-500 text-white font-semibold rounded-xl hover:shadow-lg transition-all disabled:opacity-50 disabled:cursor-not-allowed">
            {loading ? 'Переводим...' : `Перевести ${amount ? parseFloat(amount).toLocaleString() + ' ₽' : ''}`}
          </button>
        </div>
      </div>
    </div>
  )
}