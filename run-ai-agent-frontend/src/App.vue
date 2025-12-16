<template>
  <div class="layout">
    <header class="topbar">
      <div class="brand">AI 多应用实验室</div>
      <nav class="nav">
        <RouterLink to="/">主页</RouterLink>
        <RouterLink to="/love">AI 恋爱大师</RouterLink>
        <RouterLink to="/manus">AI 超级智能体</RouterLink>
      </nav>
    </header>
    <main class="page">
      <RouterView />
    </main>
    <footer class="footer">
      <div>© {{ new Date().getFullYear() }} AI 多应用实验室 | 保留所有权利</div>
    </footer>
  </div>
</template>

<script setup>
import { RouterLink, RouterView } from 'vue-router';
</script>

