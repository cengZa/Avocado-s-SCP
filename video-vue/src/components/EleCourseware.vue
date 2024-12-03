<template>
  <div class="resource-manager">
    <h1>课程资源</h1>
    <div class="container">
      <div class="sidebar">
        <h2>文件夹列表</h2>
        <ul>
          <tree-item
            v-for="folder in folderStructure"
            :key="folder.id"
            :folder="folder"
            @select-folder="getSubFolder"
          />
        </ul>
        <!-- Create folder button is visible only if the user is a teacher -->
        <button v-if="isTeacher" @click="createFolder(currentFolderId)">创建文件夹</button>
      </div>

      <div class="main-content">
        <h2>文件列表</h2>
        <div class="file-actions">
          <div class="search-container">
            <input type="text" v-model="searchTerm" placeholder="输入文件名进行查询" />
            <button @click="searchFiles(currentFolderId)">🔍</button>
          </div>
          <!-- Upload file button is visible only if the user is a teacher -->
          <button v-if="isTeacher" @click="uploadFile(currentFolderId)">上传文件</button>
        </div>
        <table>
          <thead>
            <tr>
              <th>文件名</th>
              <th>下载</th>
              <!-- Delete button is visible only if the user is a teacher -->
              <th v-if="isTeacher">删除</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="file in folderContents" :key="file.id">
              <td>{{ file.fileName }}</td>
              <td>
                <button @click="downloadFile(file.id)">下载</button>
              </td>
              <!-- Delete button is visible only if the user is a teacher -->
              <td v-if="isTeacher">
                <button @click="deleteFile(file.id)">删除</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import TreeItem from "@/components/TreeItem";
import qs from 'qs';

export default {
  components: {
    TreeItem,
  },
  data() {
    return {
      folderStructure: [],  // 文件夹结构
      folderContents: [],    // 当前文件夹内容
      currentFolderId: null, // 当前点击的文件夹
      searchTerm: '',        // 搜索关键词
      isTeacher: false,      // 用户角色标识（是否为老师）
    };
  },
  methods: {
    getFolderStructure() {
      // 获取文件夹结构
      axios.get('api/resource/get-folder-structure?classId=1')
        .then(response => {
          this.folderStructure = response.data.data;
        })
        .catch(error => {
          console.error('Error fetching folder structure:', error);
        });
    },
    getSubFolder(folder) {
      this.currentFolderId = folder.id;
      console.log("当前文件夹 ID:", this.currentFolderId); // Print the current folder ID
      axios.get(`api/resource/get-subfolder?parentFolderId=${folder.id}`)
        .then(response => {
          const subFolderArray = response.data.data;
          if (Array.isArray(subFolderArray) && subFolderArray.length > 0) {
            const findFolderInStructure = (folders, id) => {
              for (const f of folders) {
                if (f.id === id) {
                  return f;
                }
                if (f.children) {
                  const found = findFolderInStructure(f.children, id);
                  if (found) return found;
                }
              }
              return null;
            };

            const selectedFolder = findFolderInStructure(this.folderStructure, folder.id);
            if (selectedFolder) {
              if (!selectedFolder.children) {
                selectedFolder.children = [];
              }
              subFolderArray.forEach(subFolder => {
                if (!selectedFolder.children.some(child => child.id === subFolder.id)) {
                  selectedFolder.children.push(subFolder);
                }
              });
              console.log("更新后的文件夹结构:", this.folderStructure);
            } else {
              console.warn("未找到匹配的文件夹，无法更新子文件夹");
            }
            this.getSubFiles(folder);
          } else {
            this.getSubFiles(folder);
            console.error('Error: subFolderArray is empty or not an array');
          }
        })
        .catch(error => {
          console.error('Error fetching subfolder contents:', error);
        });
    },
    getSubFiles(folder) {
      axios.get(`api/resource/get-folder-files?parentFolderId=${folder.id}`)
        .then(response => {
          this.folderContents = response.data.data;
        })
        .catch(error => {
          console.error('Error fetching files in folder:', error);
        });
    },
    downloadFile(fileId) {
      axios({
        url: `api/resource/download-file?fileId=${fileId}`,
        method: 'GET',
        responseType: 'blob',
      })
      .then(response => {
        if (response.status === 200) {
          const url = window.URL.createObjectURL(new Blob([response.data]));
          const link = document.createElement('a');
          link.href = url;

          const contentDisposition = response.headers['content-disposition'];
          let fileName = 'downloadedFile.docx'; // Default filename
          if (contentDisposition) {
            const matches = contentDisposition.match(/filename="([^"]+)"/);
            if (matches && matches[1]) {
              fileName = matches[1];
            }
          }

          link.setAttribute('download', fileName);
          document.body.appendChild(link);
          link.click();
          document.body.removeChild(link);
          window.URL.revokeObjectURL(url);
        } else {
          console.error('Download failed:', response.data);
        }
      })
      .catch(error => {
        console.error('Error downloading file:', error.response ? error.response.data : error);
      });
    },
    createFolder(folderId) {
      const folderName = prompt("请输入新文件夹名称:");
      const parentFolderId = folderId;

      if (folderName) {
        axios.post('api/resource/create-folder', qs.stringify({
          classId: 1,
          parentFolderId: parentFolderId,
          folderName: folderName
        }), {
          headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
          }
        })
        .then(response => {
          alert(response.data.msg);
        })
        .catch(error => {
          console.error('Error creating folder:', error.response ? error.response.data : error);
        });
      }
    },
    searchFiles(folderId) {
      axios.get(`api/resource/search-folder-file?parentFolderId=${folderId}&searchTerm=${this.searchTerm}`)
        .then(response => {
          this.folderContents = response.data.data; // 更新文件列表
        })
        .catch(error => {
          console.error('Error searching files:', error.response ? error.response.data : error);
        });
    },
    deleteFile(fileId) {
      if (confirm("确定要删除这个文件吗？")) {
        axios.delete(`api/resource/delete-folder-file?fileId=${fileId}`)
          .then(response => {
            alert('文件删除成功!');
            this.getSubFiles(this.currentFolderId); // 刷新文件列表
          })
          .catch(error => {
            console.error('删除失败:', error.response ? error.response.data : error);
          });
      }
    },
    uploadFile(folderId) {
      const fileInput = document.createElement('input');
      fileInput.type = 'file';
      fileInput.multiple = true;
      fileInput.onchange = (event) => {
        const files = event.target.files;
        if (files.length > 0) {
          const formData = new FormData();
          for (const file of files) {
            formData.append('files', file);
          }
          formData.append('classId', 1);
          formData.append('parentFolderId', folderId);

          axios.post('api/resource/upload-files', formData, {
            headers: {
              'Content-Type': 'multipart/form-data'
            }
          })
          .then(response => {
            alert('文件上传成功!');
            this.getSubFiles(folderId);
          })
          .catch(error => {
            console.error('上传失败:', error);
          });
        }
      };
      fileInput.click();
    }
  },
  mounted() {
//判断是不是老师
     const role = JSON.parse(sessionStorage.getItem('userInfo')).role; // 获取用户信息

    this.isTeacher = role === 'Teacher';
   console.log("判断是不是老师",this.isTeacher);
    this.getFolderStructure(); // 初始化时获取文件夹结构
  },
};
</script>
<style scoped>
.container {
  display: flex;
}
.sidebar {
  width: 25%;
  border-right: 1px solid #ccc;
  padding: 20px;
}
.main-content {
  width: 75%;
  padding: 20px;
}
.file-actions {
  display: flex;
  flex-direction: column; /* 确保按钮垂直排列 */
  margin-bottom: 20px; /* 添加下方间距 */
}
.search-container {
  display: flex;
  align-items: center; /* 垂直居中对齐 */
  margin-bottom: 10px; /* 输入框与按钮之间的间距 */
}
.file-actions input {
  margin-right: 10px; /* 输入框与查询按钮之间的间距 */
}
button {
  margin-bottom: 10px; /* 按钮之间的间距 */
  max-width: 150px; /* 设置最大宽度 */
}
table {
  width: 100%;
  border-collapse: collapse;
}
th, td {
  border: 1px solid #ccc;
  padding: 8px;
  text-align: left;
}
th {
  background-color: #f2f2f2;
}
</style>
