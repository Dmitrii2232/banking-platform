// ====================
// КОНФИГУРАЦИЯ ШРИФТОВ
// ====================

import { Inter, Roboto, Roboto_Mono, Open_Sans, Montserrat } from 'next/font/google'

// Основной шрифт - Inter
export const inter = Inter({
  subsets: ['latin', 'cyrillic'],
  display: 'swap',
  variable: '--font-inter',
  weight: ['300', '400', '500', '600', '700', '800'],
  preload: true,
})

// Дополнительный шрифт для заголовков
export const roboto = Roboto({
  subsets: ['latin', 'cyrillic'],
  display: 'swap',
  variable: '--font-roboto',
  weight: ['300', '400', '500', '700', '900'],
  preload: false,
})

// Моноширинный шрифт для кода, цифр
export const robotoMono = Roboto_Mono({
  subsets: ['latin', 'cyrillic'],
  display: 'swap',
  variable: '--font-roboto-mono',
  weight: ['300', '400', '500', '600', '700'],
  preload: false,
})

// Альтернативный шрифт для UI
export const openSans = Open_Sans({
  subsets: ['latin', 'cyrillic'],
  display: 'swap',
  variable: '--font-open-sans',
  weight: ['300', '400', '500', '600', '700', '800'],
  preload: false,
})

// Шрифт для логотипов и акцентов
export const montserrat = Montserrat({
  subsets: ['latin', 'cyrillic'],
  display: 'swap',
  variable: '--font-montserrat',
  weight: ['300', '400', '500', '600', '700', '800', '900'],
  preload: false,
})

// ====================
// КОНСТАНТЫ ШРИФТОВ
// ====================

export const FONT_SIZES = {
  xs: 'text-xs',      // 12px
  sm: 'text-sm',      // 14px
  base: 'text-base',  // 16px
  lg: 'text-lg',      // 18px
  xl: 'text-xl',      // 20px
  '2xl': 'text-2xl',  // 24px
  '3xl': 'text-3xl',  // 30px
  '4xl': 'text-4xl',  // 36px
  '5xl': 'text-5xl',  // 48px
  '6xl': 'text-6xl',  // 60px
  '7xl': 'text-7xl',  // 72px
  '8xl': 'text-8xl',  // 96px
  '9xl': 'text-9xl',  // 128px
} as const

export const FONT_WEIGHTS = {
  light: 'font-light',    // 300
  normal: 'font-normal',  // 400
  medium: 'font-medium',  // 500
  semibold: 'font-semibold', // 600
  bold: 'font-bold',      // 700
  extrabold: 'font-extrabold', // 800
  black: 'font-black',    // 900
} as const

export const LINE_HEIGHTS = {
  none: 'leading-none',      // 1
  tight: 'leading-tight',    // 1.25
  snug: 'leading-snug',      // 1.375
  normal: 'leading-normal',  // 1.5
  relaxed: 'leading-relaxed', // 1.625
  loose: 'leading-loose',    // 2
} as const

export const LETTER_SPACING = {
  tighter: 'tracking-tighter', // -0.05em
  tight: 'tracking-tight',     // -0.025em
  normal: 'tracking-normal',   // 0
  wide: 'tracking-wide',       // 0.025em
  wider: 'tracking-wider',     // 0.05em
  widest: 'tracking-widest',   // 0.1em
} as const


export const TYPOGRAPHY = {
  display: {
    '2xl': 'text-6xl lg:text-7xl font-extrabold tracking-tight',
    xl: 'text-5xl lg:text-6xl font-extrabold tracking-tight',
    lg: 'text-4xl lg:text-5xl font-extrabold tracking-tight',
    md: 'text-3xl lg:text-4xl font-bold tracking-tight',
    sm: 'text-2xl lg:text-3xl font-bold',
    xs: 'text-xl lg:text-2xl font-semibold',
  },
  
  heading: {
    '1': 'text-4xl lg:text-5xl font-bold',
    '2': 'text-3xl lg:text-4xl font-bold',
    '3': 'text-2xl lg:text-3xl font-semibold',
    '4': 'text-xl lg:text-2xl font-semibold',
    '5': 'text-lg lg:text-xl font-medium',
    '6': 'text-base lg:text-lg font-medium',
  },
  
  body: {
    lg: 'text-lg leading-relaxed',
    base: 'text-base leading-relaxed',
    sm: 'text-sm leading-relaxed',
    xs: 'text-xs leading-relaxed',
  },
  
  label: {
    lg: 'text-lg font-medium',
    base: 'text-base font-medium',
    sm: 'text-sm font-medium',
    xs: 'text-xs font-medium',
  },
  
  mono: {
    lg: 'font-mono text-lg',
    base: 'font-mono text-base',
    sm: 'font-mono text-sm',
    xs: 'font-mono text-xs',
  },
} as const


/**
 * Получение классов для типографики
 */
export function getTypography(type: keyof typeof TYPOGRAPHY, size?: string): string {
  const typography = TYPOGRAPHY[type]
  if (size && typography[size as keyof typeof typography]) {
    return typography[size as keyof typeof typography]
  }
  return typography[Object.keys(typography)[0] as keyof typeof typography]
}

/**
 * Создание градиентного текста
 */
export function gradientText(gradient: string): string {
  return `bg-gradient-to-r ${gradient} bg-clip-text text-transparent`
}

/**
 * Получение классов для чисел (моноширинный шрифт)
 */
export function numericTypography(size: keyof typeof FONT_SIZES = 'base'): string {
  return `${FONT_SIZES[size]} font-mono ${FONT_WEIGHTS.medium}`
}

/**
 * Получение классов для кнопок
 */
export function buttonTypography(size: 'sm' | 'md' | 'lg' = 'md'): string {
  const sizes = {
    sm: 'text-sm font-semibold',
    md: 'text-base font-semibold',
    lg: 'text-lg font-semibold',
  }
  return sizes[size]
}

/**
 * Получение классов для форм
 */
export function formTypography(): string {
  return 'text-sm font-medium'
}


/**
 * Генератор классов для заголовков
 */
export function headingClasses(
  level: 1 | 2 | 3 | 4 | 5 | 6 = 1,
  centered: boolean = false
): string {
  const baseClass = TYPOGRAPHY.heading[level.toString() as keyof typeof TYPOGRAPHY.heading]
  return `${baseClass} ${centered ? 'text-center' : ''}`
}

/**
 * Генератор классов для тела текста
 */
export function bodyClasses(
  size: 'lg' | 'base' | 'sm' | 'xs' = 'base',
  color: 'default' | 'muted' | 'strong' = 'default'
): string {
  const sizeClass = TYPOGRAPHY.body[size]
  const colorClass = {
    default: 'text-gray-900',
    muted: 'text-gray-600',
    strong: 'text-gray-900 font-medium',
  }[color]
  
  return `${sizeClass} ${colorClass}`
}

/**
 * Генератор классов для ссылок
 */
export function linkClasses(
  variant: 'default' | 'primary' | 'subtle' = 'default'
): string {
  const base = 'transition-colors hover:underline'
  
  switch (variant) {
    case 'primary':
      return `${base} text-blue-600 hover:text-blue-800`
    case 'subtle':
      return `${base} text-gray-600 hover:text-gray-900`
    default:
      return `${base} text-current`
  }
}

/**
 * Генератор классов для цифр (банковские суммы)
 */
export function amountTypography(
  amount: number,
  size: 'sm' | 'md' | 'lg' | 'xl' = 'md'
): string {
  const sizeClasses = {
    sm: 'text-base',
    md: 'text-xl',
    lg: 'text-3xl',
    xl: 'text-4xl',
  }
  
  const color = amount >= 0 ? 'text-green-600' : 'text-red-600'
  const weight = 'font-bold'
  const mono = 'font-mono tracking-tight'
  
  return `${sizeClasses[size]} ${color} ${weight} ${mono}`
}


export default {
  // Шрифты Google
  inter,
  roboto,
  robotoMono,
  openSans,
  montserrat,
  
  // Константы
  FONT_SIZES,
  FONT_WEIGHTS,
  LINE_HEIGHTS,
  LETTER_SPACING,
  TYPOGRAPHY,
  
  // Функции
  getTypography,
  gradientText,
  numericTypography,
  buttonTypography,
  formTypography,
  headingClasses,
  bodyClasses,
  linkClasses,
  amountTypography,
}