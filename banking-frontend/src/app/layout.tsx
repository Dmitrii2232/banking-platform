import type { Metadata } from 'next'
import { Inter } from 'next/font/google'
import './globals.css'
import Header from '@/components/Header'
import Footer from '@/components/Footer'
import FloatingElements from '@/components/FloatingElements'

const inter = Inter({ 
  subsets: ['latin', 'cyrillic'],
  display: 'swap',
})

export const metadata: Metadata = {
  title: 'ТБанк | Современный банкинг',
  description: 'Инновационные финансовые решения для вашего успеха',
  keywords: 'банк, кредиты, вклады, ипотека, онлайн-банкинг',
}

export default function RootLayout({
  children,
}: {
  children: React.ReactNode
}) {
  return (
    <html lang="ru" className={inter.className}>
      <body className="min-h-screen flex flex-col bg-gradient-to-b from-blue-50 to-white">
        <FloatingElements />
        <Header />
        <main className="flex-grow">
          {children}
        </main>
        <Footer />
        
        {/* Chat bot button */}
        <button className="fixed bottom-6 right-6 w-14 h-14 bg-bank-primary rounded-full shadow-lg hover:shadow-xl transition-all duration-300 hover:scale-110 group z-50">
          <div className="relative w-full h-full flex items-center justify-center">
            <div className="absolute inset-0 bg-white/20 rounded-full pulse-ring"></div>
            <svg className="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 10h.01M12 10h.01M16 10h.01M9 16H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-5l-5 5v-5z" />
            </svg>
          </div>
          <span className="absolute -top-10 right-0 bg-gray-900 text-white text-sm px-3 py-1 rounded-lg opacity-0 group-hover:opacity-100 transition-opacity whitespace-nowrap">
            Онлайн поддержка
          </span>
        </button>
      </body>
    </html>
  )
}