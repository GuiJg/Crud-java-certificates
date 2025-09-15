# Gerenciador de Certificados Digitais (Projeto de Estudo)

> **Aviso:** este projeto foi desenvolvido apenas para **fins de estudo** e não deve ser utilizado em produção sem revisão de segurança, testes adicionais e adequações legais.

---

## 📌 Sobre o projeto
O sistema consiste em um **armazenador e gerenciador de certificados digitais** para uso interno em empresas.  
Objetivo principal: evitar que colaboradores precisem armazenar certificados digitais em suas máquinas locais, centralizando-os no sistema com **mais confiabilidade, controle e segurança**.

### Funcionalidades principais
- Autenticação de usuários com **JWT**.
- Cadastro de usuários restrito ao **administrador**.
- Upload de certificados digitais (arquivos armazenados no **Cloudinary**).
- Armazenamento de metadados e registros no **MongoDB**.
- Download controlado de certificados:
  - Ao ser baixado, gera **notificação para todos os usuários**.
  - Registro de logs detalhados (quem baixou, quando e de qual IP).
- Sistema de **notificações em tempo real** (WebSocket/STOMP).
- Histórico/auditoria para fins de segurança e rastreabilidade.

---

## 🛠️ Stack técnica
- **Linguagem:** Java 20+
- **Framework:** Spring Boot 3.x
- **Banco de dados:** MongoDB
- **Armazenamento de arquivos:** Cloudinary
- **Segurança:** Spring Security (JWT + BCrypt)
- **Mensageria em tempo real:** Spring WebSocket (STOMP)
- **Build:** Maven
- **IDE utilizada:** IntelliJ IDEA

---

## 🏗️ Arquitetura (visão geral)
1. **Backend (Spring Boot)**  
   - API REST para autenticação, usuários, certificados e notificações.  
   - Upload → salva arquivo no **Cloudinary**, metadados no **MongoDB**.  
   - Download → registra evento, gera notificação e log de auditoria.  

2. **MongoDB**  
   - Armazena usuários, certificados, notificações e logs de download.  

3. **Cloudinary**  
   - Armazena arquivos binários de certificados de forma segura.  

4. **Notificações (WebSocket)**  
   - Notificação em tempo real a cada download de certificado.  

---

## 🔒 Segurança implementada
- Autenticação via **JWT** (Bearer Token).
- Senhas armazenadas com **BCrypt**.
- Controle de acesso baseado em papéis (**ROLE_ADMIN**, **ROLE_USER**).
- Registro de logs de auditoria em cada download:
  - ID do usuário
  - IP de origem
  - User-Agent
  - Data/hora
- Recomendação de uso de **HTTPS** em ambientes produtivos.
- **Signed URLs temporários** para acesso aos arquivos no Cloudinary.

---

### Pré-requisitos
- Java 20+
- Maven
- Git
- MongoDB (cluster)
- Conta no Cloudinary 

---

## 🧪 Status do projeto
✔️ Projeto funcional em ambiente local.  
⚠️ Apenas para **fins de estudo**.  

---

## 📜 Licença
Este projeto é de uso **educacional** e não possui licença para uso comercial sem autorização prévia.
