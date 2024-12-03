<template>
  <li>
    <span @click="toggle" :class="{ 'folder': true, 'active': isActive }">
      <i :class="isOpen ? 'folder-open-icon' : 'folder-closed-icon'"></i>
      {{ folder.fileName }}
    </span>
    <ul v-show="isOpen">
      <tree-item
        v-for="child in folder.children"
        :key="child.id"
        :folder="child"
        @select-folder="handleSelectFolder"
      />
    </ul>
  </li>
</template>

<script>
export default {
  props: {
    folder: {
      type: Object,
      required: true,
    },
  },
  data() {
    return {
      isOpen: false,
      isActive: false,
    };
  },
  methods: {
    toggle() {
      this.isOpen = !this.isOpen;
      this.isActive = !this.isActive;

      // Emit event to fetch folder contents when opening
      if (this.isOpen) {
        this.$emit('select-folder', this.folder); // 传递整个folder对象
      }
    },
    handleSelectFolder(folder) {
      // 传递选中的子文件夹
      this.$emit('select-folder', folder);
    },
  },
};
</script>

<style scoped>
.folder {
  cursor: pointer;
  font-weight: bold;
}
.active {
  color: blue; /* 选中文件夹的颜色 */
}
.folder-open-icon::before {
  content: '📂'; /* 替换为打开的文件夹图标 */
}
.folder-closed-icon::before {
  content: '📁'; /* 替换为关闭的文件夹图标 */
}
</style>
