<template>
  <div id="flowHeader" class="flow-header" @click="cancelNodeSelected">
  	<!--<el-dropdown @command="handleCommand" trigger="click">
		  <span class="el-dropdown-link">
				文件<i class="el-icon-arrow-down el-icon--right"></i>
		  </span>
		  <el-dropdown-menu slot="dropdown">
		    <el-dropdown-item command="new">新建</el-dropdown-item>
		    <el-dropdown-item command="open">打开</el-dropdown-item>
		  </el-dropdown-menu>
		</el-dropdown>
    <input v-show="false" ref="file_select" type="file" @change="change"/>-->
  </div>
</template>

<script>
import { mapMutations } from 'vuex'
import xmlreader from 'xmlreader'
import fs from 'fs'
export default {
  name: 'flowHeader',
  data () {
    return {
    	path: '',
      flowTitle: '未命名表单',
      flowMenu: [{ name: '文件' }, { name: '保存' }]
    }
  },
  methods: {
    ...mapMutations('flow', ['SELECTED_NODE', 'UPDATE_NODE', 'UPDATE_LINE', 'UPDATE_COMPONENT_DATA', 'UPDATE_PROCESS']),
    ...mapMutations('processStore', ['REST', 'UPDATE_DATASOURCE_TREE']),
    /**
     * 读取配置文件
     */
    xmlRead(xmlString) {
    	const that = this
    	xmlreader.read(xmlString, function(err, res) {
				if(err !== null) {
					console.log(err)
					return
				}
				// 节点数据
				that.objsRead(res.process.node, 'node')
				// 连线数据
				that.objsRead(res.process.line, 'line')
				// 数据源
				that.objsRead(res.process.dataSource, 'dataSource')
				// 组件数据
				that.objsRead(res.process.componentData, 'componentData')
			})
    },
    objsRead(objs, lable) {
    	var that = this
    	if(objs) {
				objs.each(function(callback) {
					let id
					let obj
					if(objs.count() > 1) {
						id = objs.array[callback].attributes('id').id
						obj = JSON.parse(objs.array[callback].text())
					}
					else { // 数据只有一条时没有array
						id = objs.attributes('id').id
						obj = JSON.parse(objs.text())
					}
					if(lable === 'node') {
						that.UPDATE_NODE({
			        [id]: {
			          ...obj
							}
						})
					}
					else if(lable === 'line') {
						that.UPDATE_LINE({
			        [id]: {
			          ...obj
							}
						})
					}
					else if(lable === 'componentData') {
						that.UPDATE_COMPONENT_DATA({
			        [id]: {
			          ...obj
							}
						})
					}
					else if(lable === 'dataSource') {
						that.UPDATE_DATASOURCE_TREE({
			        [id]: {
			          ...obj
							}
						})
					}
				})
			}
    },
    /**
     * 打开的文件改变时监听事件
     */
    change() {
    	// 打开的文件是否为xml配置文件
    	if(!this.$refs.file_select.files[0]) return
    	var name = this.$refs.file_select.files[0].name
    	var suffix = name.substring(name.indexOf('.'), name.length)
    	if(suffix !== '.xml') {
    		this.cusMessage('请打开正确流程配置文件', 'error')
    		return
    	}
    	// 打开文件前先重置到最初状态
    	this.REST({})
    	let processID = 'process-' + (new Date()).valueOf()
  		let processName = name.substring(0, name.indexOf('.'))
    	this.UPDATE_PROCESS({
  			processID,
  			processName
  		})
    	this.$emit('showMain', true)
    	// xml配置文件读取
    	this.path = this.$refs.file_select.files[0].path
    	var that = this
    	fs.readFile(this.path, function (err, data) {
		   	if (err) {
	       	return console.error(err);
		   	}
		   	that.xmlRead(data.toString())
			})
    },
    // 取消选中的节点
    cancelNodeSelected () {
      let nodeId = ''
    	this.SELECTED_NODE({
        nodeId
      })
    },
    handleCommand (command) {
    	if(command === 'new') {
    		let processID = 'process-' + (new Date()).valueOf()
    		let processName
    		this.$prompt('请输入流程名称', '提示', {
	        confirmButtonText: '确定',
	        cancelButtonText: '取消'
	      })
	    	.then(({ value }) => {
	    		processName = value
	    		this.REST({})
	    		this.UPDATE_PROCESS({
	    			processID,
	    			processName
	    		})
	    		this.$emit('showMain', true)
	      })
	    	.catch(() => {
	    		return false
	      })
    	}
    	else {
    		this.$refs.file_select.click()
    	}
  	}
  }
}
</script>

<style lang="scss" scoped="scoped">
.flow-header {
  background: #1f88d6;
  color: #fff;
  position: fixed;
  top: 0px;
  left: 0px;
  z-index: 999;
  width: 100%;
  height: 30px;
  line-height: 30px;
  // display: flex;
  align-items: center;
  padding-left: 5px;
  .flow-title {
    padding: 2px 8px;
    font-size: 16px;
  }
  .flow-menu {
    display: flex;
    li {
      padding: 6px 8px;
      font-weight: bold;
      cursor: pointer;
    }
  }
  .el-dropdown-link {
  	color: #fff;
  	cursor: pointer;
  }
}
.el-popper {
	margin-top: 0px;
}
</style>
