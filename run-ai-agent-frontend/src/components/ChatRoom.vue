<template>
  <div class="chat-shell">
    <div class="chat-header">
      <div>
        <h2 style="margin: 0">{{ title }}</h2>
        <p class="status">{{ subtitle }}</p>
      </div>
      <div class="chat-id" v-if="showChatId">
        会话 ID：{{ chatId }}
      </div>
    </div>

    <div class="chat-window" ref="scrollArea">
      <div
        v-for="(item, idx) in messages"
        :key="idx"
        class="bubble"
        :class="{ user: item.sender === 'user', ai: item.sender !== 'user' }"
      >
        <div class="avatar" v-if="item.sender !== 'user'">{{ aiAvatar }}</div>
        <div class="bubble-content">
          <div class="bubble-meta">{{ item.sender === 'user' ? '我' : 'AI' }}</div>
          <div class="bubble-text">{{ item.text }}</div>
        </div>
      </div>
      <div v-if="messages.length === 0" style="color: #9ca3af">
        还没有消息，先打个招呼吧 👋
      </div>
    </div>

    <div class="chat-input">
      <textarea
        v-model="input"
        placeholder="请输入消息后回车或点击发送"
        @keyup.ctrl.enter="handleSend"
      ></textarea>
      <button :disabled="!input.trim() || isStreaming" @click="handleSend">
        {{ isStreaming ? '生成中...' : '发送' }}
      </button>
    </div>
  </div>
</template>

<script setup>
import { computed, nextTick, onBeforeUnmount, onMounted, ref } from 'vue';
import axios from 'axios';

const props = defineProps({
  title: { type: String, required: true },
  subtitle: { type: String, default: '' },
  mode: { type: String, default: 'love' }, // 'love' | 'manus' | 'manusNo'
});

const input = ref('');
const messages = ref([]);
const chatId = ref('');
const isStreaming = ref(false);
const eventSource = ref(null);
const scrollArea = ref(null);
const activeAiMessage = ref(null);
const pendingText = ref('');
const typeTimer = ref(null);
const apiBase = (import.meta.env.VITE_API_BASE || 'http://localhost:8123/api').replace(/\/$/, '');

const showChatId = computed(() => props.mode === 'love');
const aiAvatar = computed(() => {
  if (props.mode === 'love') return '💗';
  if (props.mode === 'manus') return '🤖';
  return '✨';
});

const createChatId = () =>
  `${Math.random().toString(36).slice(2, 8)}-${Date.now().toString(36)}`;

const scrollToBottom = async () => {
  await nextTick();
  const box = scrollArea.value;
  if (box) {
    box.scrollTop = box.scrollHeight;
  }
};

const closeStream = () => {
  if (eventSource.value) {
    eventSource.value.close();
    eventSource.value = null;
  }
  isStreaming.value = false;
};

const stopTypewriter = () => {
  if (typeTimer.value) {
    clearInterval(typeTimer.value);
    typeTimer.value = null;
  }
  pendingText.value = '';
};

const ensureTypewriter = (target) => {
  if (typeTimer.value) return;
  typeTimer.value = setInterval(async () => {
    if (pendingText.value.length === 0) {
      if (!isStreaming.value) {
        stopTypewriter();
      }
      return;
    }
    target.text += pendingText.value.slice(0, 1);
    pendingText.value = pendingText.value.slice(1);
    await scrollToBottom();
  }, 18);
};

const startStream = (text) => {
  stopTypewriter();
  pendingText.value = '';
  const baseUrl =
    props.mode === 'love'
      ? `${apiBase}/ai/love_app/chat/sse?message=${encodeURIComponent(text)}&chatId=${chatId.value}`
      : `${apiBase}/ai/manus/chat?message=${encodeURIComponent(text)}`;

  const aiMessage = { sender: 'ai', text: '' };
  activeAiMessage.value = aiMessage;
  messages.value.push(aiMessage);
  isStreaming.value = true;

  const es = new EventSource(baseUrl);
  eventSource.value = es;

  es.onmessage = async (evt) => {
    const raw = (evt?.data ?? '').toString();
    let done = false;
    let chunk = '';
    try {
      const parsed = JSON.parse(raw);
      done = parsed?.done === true || parsed?.finish_reason === 'stop';
      chunk = parsed?.content ?? parsed?.data ?? parsed?.text ?? '';
    } catch (e) {
      done = raw.trim() === '[DONE]';
      chunk = done ? '' : raw;
    }

    if (done) {
      closeStream();
      return;
    }
    if (props.mode === 'manus' && chunk) {
      chunk += '\n';
    }
    pendingText.value += chunk;
    ensureTypewriter(aiMessage);
  };

  es.onerror = () => {
    aiMessage.text = aiMessage.text || '连接异常，请稍后重试';
    stopTypewriter();
    closeStream();
  };

  es.onopen = () => {
    scrollToBottom();
  };
};

const requestOnce = async (text) => {
  const url = `${apiBase}/ai/manus/chat_no?message=${encodeURIComponent(text)}`;
  const aiMessage = { sender: 'ai', text: '...请求中' };
  messages.value.push(aiMessage);
  isStreaming.value = true;
  try {
    const res = await axios.get(url);
    aiMessage.text = res?.data ?? '';
  } catch (err) {
    aiMessage.text = '请求失败，请稍后重试';
  } finally {
    isStreaming.value = false;
    scrollToBottom();
  }
};

const handleSend = () => {
  const text = input.value.trim();
  if (!text || isStreaming.value) return;
  messages.value.push({ sender: 'user', text });
  input.value = '';
  scrollToBottom();
  if (props.mode === 'manusNo') {
    requestOnce(text);
  } else {
    startStream(text);
  }
};

onMounted(() => {
  if (props.mode === 'love') {
    chatId.value = createChatId();
  }
});

onBeforeUnmount(() => {
  closeStream();
  stopTypewriter();
});
</script>

