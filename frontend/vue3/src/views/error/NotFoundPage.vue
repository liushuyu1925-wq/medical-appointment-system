<template>
  <div class="not-found">
    <div class="content">
      <h1>404</h1>
      <h2>页面未找到</h2>
      <p>抱歉，您访问的页面不存在</p>
      <router-link
        to="/"
        class="back-home"
      >
        返回首页
      </router-link>
    </div>
  </div>
</template>

<script setup>
</script>

<style scoped>
.not-found {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  padding: 24px;
  background:
    radial-gradient(circle at 18% 18%, rgba(126, 177, 255, 0.18), transparent 28%),
    radial-gradient(circle at 82% 12%, rgba(255, 255, 255, 0.84), transparent 24%),
    linear-gradient(135deg, #f8fbff 0%, #eef4fb 52%, #f7f9fc 100%);
}

.content {
  width: min(100%, 520px);
  padding: 42px 32px;
}

h1 {
  font-size: clamp(88px, 16vw, 132px);
  margin: 0;
  color: var(--glass-primary-strong);
  line-height: 1;
  letter-spacing: 0;
}

h2 {
  font-size: 30px;
  margin: 18px 0 12px;
  color: var(--glass-text-primary);
}

p {
  margin-bottom: 24px;
  line-height: 1.7;
}

.back-home {
  display: inline-block;
  padding: 12px 24px;
  background: linear-gradient(135deg, #76a7ff, var(--glass-primary));
  color: white;
  text-decoration: none;
  border-radius: 999px;
  border: 1px solid rgba(93, 141, 247, 0.62);
  box-shadow: 0 12px 24px rgba(93, 141, 247, 0.18);
  transition: transform 0.18s ease, box-shadow 0.18s ease, background 0.18s ease;
}

.back-home:hover {
  background: linear-gradient(135deg, #8bb7ff, #3f74e0);
  transform: translateY(-1px);
  box-shadow: 0 16px 30px rgba(93, 141, 247, 0.22);
}
</style> 
