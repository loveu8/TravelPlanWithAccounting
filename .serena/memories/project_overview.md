# 專案概述

## 專案名稱
TravelPlanWithAccounting（旅遊行程與記帳）

## 專案目的
一個整合旅遊行程規劃與記帳功能的全端應用系統

## 架構
- **前後端分離架構**
- Backend: RESTful API 服務
- Frontend: Next.js Web 應用
- Database: PostgreSQL

## 多語系支持
- 繁體中文 (zh-TW) - 預設語系
- 英文 (en-US)
- Backend 透過 Accept-Language header 自動切換
- Frontend 使用 i18next

## 部署方式
- Docker 容器化部署
- 前後端獨立 Docker image
- Docker Compose 編排

## 開發環境
- 系統平台: Windows
- IDE: Visual Studio Code
- 版本控制: Git