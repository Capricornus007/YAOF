#!/usr/bin/env bash
# =============================================================================
# YAOF 調試編譯腳本
# 用途: 以詳細診斷模式執行 Gradle 構建，方便排查編譯問題。
#
# 用法:
#   ./debug_build.sh              # 預設: 調試模式 assembleDebug
#   ./debug_build.sh --clean      # 先 clean 再構建
#   ./debug_build.sh --release    # 構建 release 版本
#   ./debug_build.sh --deps       # 顯示依賴樹
#   ./debug_build.sh --scan       # 生成 Build Scan 報告
#   ./debug_build.sh --full       # 完整診斷: clean + dep-tree + buildScan
#   ./debug_build.sh --help       # 顯示幫助
# =============================================================================

set -euo pipefail

PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$PROJECT_DIR"

# --------------- 預設值 ---------------
TASK="assembleDebug"
DO_CLEAN=false
DO_DEPS=false
DO_SCAN=false

# 基礎調試參數 (總是啟用)
BASE_ARGS="--stacktrace --warning-mode all --console=verbose"

# --------------- 解析參數 ---------------
for arg in "$@"; do
    case "$arg" in
        --clean)
            DO_CLEAN=true
            ;;
        --release)
            TASK="assembleRelease"
            ;;
        --deps)
            DO_DEPS=true
            ;;
        --scan)
            DO_SCAN=true
            ;;
        --full)
            DO_CLEAN=true
            DO_DEPS=true
            DO_SCAN=true
            ;;
        --help|-h)
            sed -n '2,12p' "${BASH_SOURCE[0]}"
            exit 0
            ;;
        *)
            echo "❌ 未知參數: $arg"
            echo "   使用 --help 查看可用選項"
            exit 1
            ;;
    esac
done

# --------------- 執行 ---------------
echo "╔══════════════════════════════════════════════╗"
echo "║       YAOF 調試編譯                          ║"
echo "╠══════════════════════════════════════════════╣"
echo "║  專案目錄 : $PROJECT_DIR"
echo "║  目標任務 : $TASK"
echo "║  Clean    : $DO_CLEAN"
echo "║  依賴樹   : $DO_DEPS"
echo "║  BuildScan: $DO_SCAN"
echo "╚══════════════════════════════════════════════╝"
echo ""

# --- 步驟 1: Clean（如果需要）---
if $DO_CLEAN; then
    echo "🧹 [1/3] 清理建置快取..."
    ./gradlew clean $BASE_ARGS
    echo ""
fi

# --- 步驟 2: 主構建 ---
STEP=1
if $DO_CLEAN; then STEP=2; fi
echo "🔨 [$STEP/3] 執行 $TASK ..."
SCAN_ARG=""
if $DO_SCAN; then
    SCAN_ARG="--scan"
fi

./gradlew $TASK $BASE_ARGS $SCAN_ARG
echo ""

# --- 步驟 3: 依賴樹（如果需要）---
if $DO_DEPS; then
    STEP=3
    echo "📦 [$STEP/3] 顯示依賴樹..."

    # 收集所有模塊的依賴
    MODULES=$(./gradlew -q projects 2>/dev/null | grep "--- Project" | awk -F"'" '{print $2}' || echo ":app")

    for mod in $MODULES; do
        echo ""
        echo "── 模塊: ${mod#:} ──────────────────────────────"
        ./gradlew "${mod#:}:dependencies" --configuration debugRuntimeClasspath $BASE_ARGS 2>&1 | head -200
    done
    echo ""
fi

echo "✅ 調試編譯完成"
