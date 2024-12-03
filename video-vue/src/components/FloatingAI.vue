<template>
  <div id="app">
    <img
        class="floating-ai"
        :style="{ left: ballX + 'px', top: ballY + 'px' }"
        @mousedown="startDrag"
        @click.stop.prevent="toggleDialog"
        src="@/assets/six.png"
        alt="Chat Icon"
    />

    <div
        class="dialog-box"
        :class="{ 'fullscreen': isFullScreen }"
        v-if="isDialogVisible"
        :style="isFullScreen
        ? { left: '0', top: '0', width: '100vw', height: '96vh' }
        : { left: (ballX - 250) + 'px', top: (ballY - 400) + 'px', width: dialogWidth + 'px', height: dialogHeight + 'px' }"
    >
      <div class="header">
        <button @click.prevent="toggleSize" class="toggle-button">
          {{ isFullScreen ? "🔽" : "🔍" }}
        </button>
      </div>
      <div class="messages" ref="messagesContainer">
        <div v-for="(msg, index) in messages" :key="index"
             :class="{ 'user-message': msg.isUser, 'ai-message': !msg.isUser }">
          {{ msg.text }}
        </div>
      </div>
      <textarea
          ref="inputBox"
          v-model="userInput"
          placeholder="请输入你的对话..."
          @keyup.enter.prevent="sendMessage"
          rows="1"
          @input='autoResize'
          class='input-box'
          style="width: calc(100% - 10px); box-sizing: border-box;"
      ></textarea>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      isDialogVisible: false,
      userInput: '',
      messages: [],
      isDragging: false,
      ballX: window.innerWidth - 80,
      ballY: window.innerHeight - 100,
      dialogWidth: 300,
      dialogHeight: 400,
      isFullScreen: false,
      offsetX: 0,
      offsetY: 0,
      defaultInputHeight: 'auto',
      stringA :''
    };
  },
  methods: {
    toggleDialog() {
      this.isDialogVisible = !this.isDialogVisible;
    },
    toggleSize() {
      this.isFullScreen = !this.isFullScreen;
    },
    async sendMessage() {
      if (this.userInput.trim()) {
        // 清空用户输入
        const userInput = this.userInput;
        this.userInput = '';

        // 添加用户消息
        this.messages.push({ text: userInput, isUser: true });

        // 发送请求到后端
        try {
          const response = await axios.post('/api/ask', {
            question: userInput
          });

          if (response.status === 200) {
            const aiResponse = response.data;

            this.messages.push({ text: '', isUser: false });

            let i = 0;
            const messageIndex = this.messages.length - 1;

            const typeWriter = () => {
              if (i < aiResponse.length) {
                this.messages[messageIndex].text += aiResponse.charAt(i);
                i++;
                setTimeout(typeWriter, 20);
              }
            };
            // 启动打字效果
            typeWriter();
          } else {
            console.error('Error fetching AI response:', response.statusText);
          }
        } catch (error) {
          console.error('Error:', error);
        }

        this.$nextTick(() => {
          this.$refs.inputBox.style.height = this.defaultInputHeight;
          this.scrollToBottom();
        });
      }
    },
    autoResize(event) {
      event.target.style.height = "auto";
      event.target.style.height = `${event.target.scrollHeight}px`; // 设置为内容高度
    },
    scrollToBottom() {
      const container = this.$refs.messagesContainer;
      container.scrollTop = container.scrollHeight;
    },
    startDrag(event) {
      this.isDragging = true;
      this.offsetX = event.clientX - this.ballX;
      this.offsetY = event.clientY - this.ballY;

      document.addEventListener('mousemove', this.drag);
      document.addEventListener('mouseup', this.stopDrag);
    },
    drag(event) {
      if (this.isDragging) {
        this.ballX = event.clientX - this.offsetX;
        this.ballY = event.clientY - this.offsetY;
      }
    },
    stopDrag() {
      this.isDragging = false;
      document.removeEventListener('mousemove', this.drag);
      document.removeEventListener('mouseup', this.stopDrag);
    },
  },
};
</script>

<style scoped>
.floating-ai {
  position: absolute;
  width: 60px;
  height: 80px;
  z-index: 9999;
  cursor: pointer;
}

.dialog-box {
  position: absolute;
  width: 300px;
  height: 400px;
  bottom: 20px;
  right: 20px;
  background-color: white;
  border-radius: .5rem;
  box-shadow: rgba(0, 0, 0, 0.2) -1px -1px, rgba(0, 0, 0, 0.14) -2px -2px, rgba(0, 0, 0, 0.12) -4px -4px;
  transition: all 0.3s ease;
}

.dialog-box.fullscreen {
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  width: 100vw;
  height: 100vh;
  margin: 0;
  border-radius: 0;
}

.header {
  display: flex;
  justify-content: flex-end;
  padding: 0.5rem;
}

.toggle-button {
  background: none;
  border: none;
  cursor: pointer;
  font-size: 24px; /* Adjusted for emoji size */
}

.messages {
  height: calc(100% - 40px);
  overflow-y: auto;
  padding: .5rem;
  white-space: pre-wrap;
  text-align: left;
}

.user-message, .ai-message {
  margin-bottom: .5rem;
  padding: .5rem;
  border-radius: .5rem;
  text-align: left;
  word-wrap: break-word;
}

.user-message {
  background-color: #e1ffc7;
}

.ai-message {
  background-color: #f1f1f1;
}

textarea {
  width: 100%;
  padding: .5rem;
  border-top: 1px solid #ddd;
  box-sizing: border-box;
}
</style>
