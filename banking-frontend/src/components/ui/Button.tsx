'use client'

import { motion, MotionProps } from 'framer-motion'
import { LucideIcon } from 'lucide-react'
import { ButtonHTMLAttributes, forwardRef } from 'react'

// Создаем кастомный интерфейс, который исключает конфликтные пропсы
type ButtonBaseProps = Omit<ButtonHTMLAttributes<HTMLButtonElement>, 
  'onDrag' | 'onDragStart' | 'onDragEnd' | 'onAnimationStart' | 
  'onAnimationEnd' | 'onDragTransitionEnd'
>

interface ButtonProps extends ButtonBaseProps {
  variant?: 'primary' | 'secondary' | 'outline' | 'ghost' | 'danger' | 'success'
  size?: 'sm' | 'md' | 'lg' | 'xl'
  isLoading?: boolean
  leftIcon?: LucideIcon
  rightIcon?: LucideIcon
  fullWidth?: boolean
  rounded?: 'sm' | 'md' | 'lg' | 'full'
  animation?: 'scale' | 'bounce' | 'none'
  gradient?: boolean
  shadow?: boolean
}

// Создаем MotionButton с правильными пропсами
const MotionButton = motion.button

const Button = forwardRef<HTMLButtonElement, ButtonProps>(
  (
    {
      children,
      variant = 'primary',
      size = 'md',
      isLoading = false,
      leftIcon: LeftIcon,
      rightIcon: RightIcon,
      fullWidth = false,
      rounded = 'md',
      animation = 'scale',
      gradient = false,
      shadow = true,
      className = '',
      disabled,
      type = 'button',
      ...props
    },
    ref
  ) => {
    // Базовые стили
    const baseStyles = 'inline-flex items-center justify-center font-medium transition-all duration-300 focus:outline-none focus:ring-2 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed'
    
    // Варианты кнопок
    const variants = {
      primary: gradient 
        ? 'bg-gradient-to-r from-blue-600 to-cyan-500 text-white hover:from-blue-700 hover:to-cyan-600 focus:ring-blue-500' 
        : 'bg-blue-600 text-white hover:bg-blue-700 focus:ring-blue-500',
      secondary: 'bg-gray-100 text-gray-900 hover:bg-gray-200 focus:ring-gray-500',
      outline: 'border border-blue-500 text-blue-500 hover:bg-blue-50 focus:ring-blue-500',
      ghost: 'text-gray-700 hover:bg-gray-100 focus:ring-gray-500',
      danger: 'bg-red-500 text-white hover:bg-red-600 focus:ring-red-500',
      success: 'bg-green-500 text-white hover:bg-green-600 focus:ring-green-500'
    }

    // Размеры
    const sizes = {
      sm: 'px-3 py-1.5 text-sm',
      md: 'px-4 py-2 text-base',
      lg: 'px-6 py-3 text-lg',
      xl: 'px-8 py-4 text-xl'
    }

    // Закругление
    const borderRadius = {
      sm: 'rounded',
      md: 'rounded-lg',
      lg: 'rounded-xl',
      full: 'rounded-full'
    }

    // Собираем все классы
    const buttonClasses = [
      baseStyles,
      variants[variant],
      sizes[size],
      borderRadius[rounded],
      shadow ? 'shadow-md hover:shadow-lg' : '',
      fullWidth ? 'w-full' : '',
      className
    ].filter(Boolean).join(' ')

    // Содержимое кнопки
    const buttonContent = (
      <>
        {isLoading ? (
          <>
            <svg className="animate-spin -ml-1 mr-2 h-4 w-4 text-current" fill="none" viewBox="0 0 24 24">
              <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
              <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
            Загрузка...
          </>
        ) : (
          <>
            {LeftIcon && <LeftIcon className="mr-2 h-4 w-4" />}
            {children}
            {RightIcon && <RightIcon className="ml-2 h-4 w-4" />}
          </>
        )}
      </>
    )

    // Если анимация отключена или кнопка загружается/отключена
    if (animation === 'none' || disabled || isLoading) {
      return (
        <button
          ref={ref}
          className={buttonClasses}
          disabled={disabled || isLoading}
          type={type}
          {...props}
        >
          {buttonContent}
        </button>
      )
    }

    // Настраиваем анимацию
    const animationProps: MotionProps = animation === 'scale' 
      ? {
          whileHover: { scale: 1.05 },
          whileTap: { scale: 0.95 }
        }
      : {
          whileHover: { y: -2 },
          whileTap: { y: 0 }
        }

    return (
      <MotionButton
        ref={ref}
        className={buttonClasses}
        disabled={disabled}
        type={type}
        {...animationProps}
        // Приводим props к MotionProps, исключая конфликтные
        {...(props as any)}
      >
        {buttonContent}
      </MotionButton>
    )
  }
)

Button.displayName = 'Button'

export default Button