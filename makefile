.PHONY: help build test run stop clean logs status

GREEN  := $(shell tput -Txterm setaf 2)
YELLOW := $(shell tput -Txterm setaf 3)
RED    := $(shell tput -Txterm setaf 1)
RESET  := $(shell tput -Txterm sgr0)

DOCKER_COMPOSE = docker-compose
MVN = mvn
ROOT_DIR = $(shell pwd)

help: ## Показать справку
	@awk 'BEGIN {FS = ":.*?## "} /^[a-zA-Z_-]+:.*?## / {printf "  ${GREEN}%-25s${RESET} %s\n", $$1, $$2}' $(MAKEFILE_LIST)

# ===== СБОРКА =====

build-proto: ## Собрать proto-контракты
	cd banking-proto && $(MVN) clean install -DskipTests && cd $(ROOT_DIR)

build-auth: build-proto ## Собрать Auth Server
	cd banking-infra/banking-auth-server && $(MVN) clean package -DskipTests && cd $(ROOT_DIR)

build-resource: build-proto ## Собрать Resource Server
	cd banking-infra/banking-resource-server && $(MVN) clean package -DskipTests && cd $(ROOT_DIR)

build-gateway: ## Собрать API Gateway
	cd banking-infra/banking-api-gateway && $(MVN) clean package -DskipTests && cd $(ROOT_DIR)

build-all: build-proto build-auth build-resource build-gateway ## Собрать всё

# ===== ЗАПУСК =====

clean-old: ## Удалить старые контейнеры
	@docker rm -f bank-zookeeper bank-kafka bank-kafka-ui 2>/dev/null || true
	@docker rm -f bank-postgres-eventstore bank-postgres-accounting bank-postgres-auth 2>/dev/null || true
	@docker rm -f bank-redis bank-jaeger bank-prometheus bank-grafana 2>/dev/null || true
	@docker rm -f banking-auth-server banking-resource-server banking-api-gateway 2>/dev/null || true

infra-up: clean-old ## Запустить инфраструктуру
	$(DOCKER_COMPOSE) up -d zookeeper kafka kafka-ui
	$(DOCKER_COMPOSE) up -d postgres-eventstore postgres-accounting postgres-auth redis
	@sleep 20
	@echo "${GREEN}Инфраструктура готова${RESET}"

monitoring-up: ## Запустить мониторинг
	$(DOCKER_COMPOSE) up -d jaeger prometheus grafana

auth-up: build-auth ## Запустить Auth Server
	$(DOCKER_COMPOSE) up -d --build banking-auth-server
	@sleep 10

resource-up: build-resource ## Запустить Resource Server
	$(DOCKER_COMPOSE) up -d --build banking-resource-server
	@sleep 10

gateway-up: build-gateway ## Запустить API Gateway
	$(DOCKER_COMPOSE) up -d --build banking-api-gateway
	@sleep 10

up: clean-old infra-up monitoring-up auth-up resource-up gateway-up ## Запустить всё
	@echo "${GREEN}=== Платформа запущена ===${RESET}"
	@make status

# ===== СТАТУС =====

status: ## Состояние сервисов
	@$(DOCKER_COMPOSE) ps --format "table {{.Name}}\t{{.Status}}\t{{.Ports}}"

health: ## Health check
	@echo -n "Gateway:  " && curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/actuator/health 2>/dev/null && echo "" || echo "DOWN"
	@echo -n "Auth:     " && curl -s -o /dev/null -w "%{http_code}" http://localhost:8085/actuator/health 2>/dev/null && echo "" || echo "DOWN"
	@echo -n "Resource: " && curl -s -o /dev/null -w "%{http_code}" http://localhost:8086/actuator/health 2>/dev/null && echo "" || echo "DOWN"

# ===== ОСТАНОВКА =====

stop: ## Остановить всё
	$(DOCKER_COMPOSE) down

clean: stop ## Остановить и очистить
	$(DOCKER_COMPOSE) down -v

# ===== ЛОГИ =====

logs: ## Все логи
	$(DOCKER_COMPOSE) logs -f

logs-gateway: ## Логи Gateway
	$(DOCKER_COMPOSE) logs -f banking-api-gateway

logs-auth: ## Логи Auth
	$(DOCKER_COMPOSE) logs -f banking-auth-server

logs-resource: ## Логи Resource
	$(DOCKER_COMPOSE) logs -f banking-resource-server