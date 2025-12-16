<template>
  <div>
    <h1 style="margin-top: 0">选择一个 AI 应用</h1>
    <p style="color: #6b7280; margin-top: 4px">
      主页可快速切换不同场景，点击卡片进入对应的聊天页面。
    </p>
    <div class="cards" style="margin-top: 16px">
      <RouterLink to="/love" class="card">
        <h3 class="card-title">AI 恋爱大师</h3>
        <p class="card-desc">模拟恋爱聊天，提供情感互动与建议。</p>
        <span class="primary-button">进入聊天</span>
      </RouterLink>
      <RouterLink to="/manus" class="card">
        <h3 class="card-title">AI 超级智能体</h3>
        <p class="card-desc">多工具能力的智能体，实时流式回复。</p>
        <span class="primary-button">进入聊天</span>
      </RouterLink>
    </div>
  </div>
</template>

<script setup>
</script>

