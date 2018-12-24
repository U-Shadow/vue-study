<template>
  <div id="flowTree" class="flow-tree">
    <div class="flt-container">
      <ul>
      	<!-- 流程  -->
      	<li class="flt-tool-list">
          <div class="flt-list-tit" @click.stop="processItems.show = !processItems.show"  @contextmenu="showMenu">
          	<vue-context-menu :contextMenuData="processMenuData" @addProcess="addProcess">
    				</vue-context-menu>
            <icon :name="processItems.show?'down-arrow':'up-arrow'" :size="8" class="flt-icon"></icon>
            <span>{{processItems.title}}</span>
          </div>
          <div class="flt-shape-list" v-show="processItems.show">
            <div class="flt-item-box">
            	<div :class="activeIndex === index ? 'li-list-item active': 'li-list-item'"
            		v-for="(item,index) in processTree" :key="index" @dblclick="changeCurrPorcess(index, item.processName)" 
            		@contextmenu="showChildProcessMenu" @mousedown="processMouseDown(index)">
          			<icon name="right-arrow" :size="8" style="margin-left: 8px; margin-bottom: 2px;"></icon>
          			<span>{{item.processName}}</span>
              </div>
              <vue-context-menu :contextMenuData="childProcessMenuData" @addProcess="addProcess"
              	@removeProcess="removeProcess">
    					</vue-context-menu>
            </div>
          </div>
        </li>
        <!-- 数据源  -->
      	<li class="flt-tool-list">
          <div class="flt-list-tit" @click.stop="dataSourceItems.show = !dataSourceItems.show"  @contextmenu="showMenu2">
          	<vue-context-menu :contextMenuData="contextMenuData" @addDataSource="addDataSource">
    				</vue-context-menu>
            <icon :name="dataSourceItems.show?'down-arrow':'up-arrow'" :size="8" class="flt-icon"></icon>
            <span>{{dataSourceItems.title}}</span>
          </div>
          <div class="flt-shape-list" v-show="dataSourceItems.show">
            <div class="flt-item-box">
            	<div :class="activeIndex2 === index ? 'li-list-item active': 'li-list-item'" @click="datasourceSelected(index)"
            		v-for="(item,index) in datasourceTree" :key="index" @mousedown="datasourceMouseDown(index)"
            		@dblclick="openDatasource(index)" @contextmenu="showDatasourceItem">
          			<!--<i class="el-icon-caret-right" style="margin-left: 8px;"></i>-->
          			<icon name="right-arrow" :size="8" style="margin-left: 8px; margin-bottom: 2px;"></icon>
          			<span>{{index}}</span>
              </div>
              <vue-context-menu :contextMenuData="childDatasourceMenuData" 
              	@addDataSource="addDataSource" @removeDatasource="removeDatasource">
    					</vue-context-menu>
            </div>
          </div>
        </li>
      </ul>
    </div>
    
    <dataSource :componentDialogTitle="componentDialogTitle" :dialogActionType="dialogActionType" :dataSourceData="dataSourceData"
    	:componentDialogVisible="componentDialogVisible" @visibleChange="visibleChange" @doClose="doClose"></dataSource>
    	
  </div>
</template>

<script>
import { mapMutations, mapState } from 'vuex'
import fs from 'fs'
import dataSource from '@/components/dialog/DataSourceComponent'

export default {
  name: 'flow-tree',
  components: {
  	dataSource
  },
  data () {
    return {
    	previousProcess: '',	// 上个活跃流程
      /* 新增流程定义数据  begin */
      activeIndex: '',
      // contextmenu data (菜单数据)
      processMenuData: {
      // the contextmenu name(@1.4.1 updated)
        menuName: 'processItems',
      // The coordinates of the display(菜单显示的位置)
        axios: {
          x: null,
          y: null
        },
      // Menu options (菜单选项)
        menulists: [
          {
            fnHandler: 'addProcess', // Binding events(绑定事件)
            icoName: '', // icon (icon图标 )
            btnName: '新增' // The name of the menu option (菜单名称)
          }
        ]
      },
      childProcessMenuData: {
        menuName: 'childProcessMenuData',
      	// The coordinates of the display(菜单显示的位置)
        axios: {
          x: null,
          y: null
        },
      	// Menu options (菜单选项)
        menulists: [
          {
            fnHandler: 'addProcess', // Binding events(绑定事件)
            icoName: '', // icon (icon图标 )
            btnName: '新增' // The name of the menu option (菜单名称)
          },
          {
            fnHandler: 'removeProcess', // Binding events(绑定事件)
            icoName: '', // icon (icon图标 )
            btnName: '删除' // The name of the menu option (菜单名称)
          }
        ]
      },
      processData: {},
      processItems: {
        title: '流程',
        show: true
      },
      /* 新增流程定义数据  end */
     	/****************************************************************************************************/
      /* 新增数据源定义数据  begin */
     	activeIndex2: '',
      // contextmenu data (菜单数据)
      contextMenuData: {
      // the contextmenu name(@1.4.1 updated)
        menuName: 'datasourceItems',
      // The coordinates of the display(菜单显示的位置)
        axios: {
          x: null,
          y: null
        },
      // Menu options (菜单选项)
        menulists: [
          {
            fnHandler: 'addDataSource', // Binding events(绑定事件)
            icoName: '', // icon (icon图标 )
            btnName: '新增' // The name of the menu option (菜单名称)
          }
        ]
      },
      childDatasourceMenuData: {
        menuName: 'childDatasourceMenuData',
      	// The coordinates of the display(菜单显示的位置)
        axios: {
          x: null,
          y: null
        },
      	// Menu options (菜单选项)
        menulists: [
          {
            fnHandler: 'addDataSource', // Binding events(绑定事件)
            icoName: '', // icon (icon图标 )
            btnName: '新增' // The name of the menu option (菜单名称)
          },
          {
            fnHandler: 'removeDatasource', // Binding events(绑定事件)
            icoName: '', // icon (icon图标 )
            btnName: '删除' // The name of the menu option (菜单名称)
          }
        ]
      },
      componentDialogTitle: '新增数据源',
      componentDialogVisible: false,
      dataSourceData: {},
      dialogActionType: true,		// 是否新增数据源true, false
      dataSourceItems: {
        title: '数据源',
        show: true
      }
      /* 新增数据源定义数据  end */
    }
  },
  computed: {
    ...mapState('flow', ['processInfo', 'currentProcess']),
    ...mapState('processStore', ['processArray', 'processTree', 'datasourceTree', 'datasourceTree'])
  },
  methods: {
    ...mapMutations('flow', ['UPDATE_PROCESS', 'CURRENT_PROCESS', 'CHANGE_PROCESS', 'REMOVE_DATASOURCE']),
    ...mapMutations('processStore', ['REST', 'UPDATE_PROCESSARRAY', 'UPDATE_PROCESS_TREE', 'UPDATE_REPLACE_PROCESS_TREE',
    	'UPDATE_REPLACE_DATASOURCE_TREE', 'REMOVE_PROCESS_TREE', 'REMOVE_DATASOURCE_TREE', 'INIT_HISTORY'
    ]),
    // 切换当前的流程
    changeCurrPorcess(processId, processName) {
    	// 本地存储中有之间从本地存储中取,否则取读取工作空间文件
    	if(this.processArray[processId]) {
    		this.$emit("showMain", true);
		  	let flowData = this.processArray[processId];
		  	this.CHANGE_PROCESS({flowData});
    	}
    	else {
    		let processXmlPath = this.$workspacePath + 'liucheng_' + processName + '/' + processId + '.xml';
    		this.readProcessFile(processId, processXmlPath);
    	}
    },
    readProcessFile(processId, target) {
    	if(!fs.existsSync(target)) {
    		this.cusMessage("没有找到该流程的流程文件", "error");
    		return;
    	}
    	var that = this;
    	fs.readFile(target, function(err, data) {
    		if(err) {
					console.error(err)
			    return console.error("读取该流程的流程文件失败");
    		}
    		let processArrayJson = that.$x2js.xml2js(data.toString())
    		let flowData = processArrayJson.config;
				that.CHANGE_PROCESS({flowData});
				let processId = that.currentProcess;
				that.INIT_HISTORY(processId);
				that.$emit('showMain', true);
    	})
    },
    selected(index) {
    	let tmp = this.activeIndex;
    	// 当前点击流程不是上个活跃流程
    	if(index != tmp) {
    		this.previousProcess = tmp;
    	}
    	// 如果上个流程被删除了
    	if(!this.processTree[this.previousProcess]) {
    		this.previousProcess = '';
    	}
			this.activeIndex = index;
//			console.log("上一个流程: "+this.previousProcess, "当前流程: "+index)
		},
    /*****************   流程鼠标右击事件相关方法   *****************/
   	processMouseDown(index) {
   		this.selected(index);
   	},
    showMenu () {
      event.preventDefault()
      var x = event.clientX
      var y = event.clientY
      // Get the current location
      this.processMenuData.axios = {
        x, y
      }
    },
    addProcess () {
  		this.$prompt('请输入流程名称', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      })
    	.then(({ value }) => {
    		let processId = 'process-' + value
    		if(this.processTree[processId]) {
    			this.cusMessage("流程已存在", "warning");
    			return;
    		}
    		// 先重置所有的流程信息
    		this.REST(processId);
    		this.UPDATE_PROCESS({
    			[processId]: {
    				processId: processId,
    				processName: value,
    				keys: {},	// 流程中节点对应变量名称对象, 节点id对应变量名称(如果一个节点会对应多个变量名称,节点后面一次加_a、_b...)
  					v_k: {}	// 流程中变量名称对应节点对象, 用于判断填写的变量名称是否存在
    			}
    		});
    		// 初始化流程的历史数据
    		this.INIT_HISTORY(processId);
    		// 工作空间根目录
	    	if(!fs.existsSync(this.$workspacePath)) {
	    		fs.mkdirSync(this.$workspacePath)
	    	}
    		this.createTreeConfig(this.processInfo, processId, this.$workspacePath + 'process_tree.xml');
    		this.createProcessSpace(value, processId);
    		this.activeIndex = processId;
    		this.$emit('showMain', true);
    	})
    	.catch(() => {
    		return false
      })
    },
    showChildProcessMenu () {
      event.preventDefault()
      var x = event.clientX
      var y = event.clientY
      // Get the current location
      this.childProcessMenuData.axios = {
        x, y
      }
    },
    // 删除流程
    removeProcess() {
    	let id = this.activeIndex;
			let xmlHeader = '<?xml version="1.0" encoding="UTF-8"?>';
    	let arrayXml;
    	let that = this;
    	let fileName = this.$workspacePath + 'process_tree.xml';
    	// 从流程树文件中删除当前流程的节点
			fs.readFile(fileName, function(err, data) {
				if (err) {
					console.error(err)
			    return console.error("没找到流程树配置文件");
			  }
				let arrayJson = that.$x2js.xml2js(data.toString())
				delete arrayJson.config[id]
				arrayXml = xmlHeader + that.$newLine + that.$x2js.js2xml(arrayJson);
				fs.writeFile(fileName, arrayXml, function(err) {
			   	if (err) {
			   		console.error(err)
		       	return console.error("创建流程树配置文件失败");
			   	}
				})
			})
			// 删除当前流程工作目录及其下所有文件
			let tmpDirpath = this.$workspacePath + 'liucheng_' + this.processTree[id].processName;
			this.delDir(tmpDirpath);
			this.REMOVE_PROCESS_TREE(id);
			// 将活跃流程修改为上一个活跃流程
    	let nowProcess = this.previousProcess;
    	if(nowProcess == '') {
    		this.$emit("showMain", false);
    	}
    	else {
    		this.changeCurrPorcess(nowProcess, this.processTree[nowProcess].processName);
    	}
    },
    /***************** 流程鼠标右击事件相关方法结束 *****************/
    // 删除文件夹及文件夹下所有文件
    delDir(path) {
			if(fs.existsSync(path)){
				let files = [];
        files = fs.readdirSync(path);
        files.forEach((file, index) => {
          let curPath = path + "/" + file;
          if(fs.statSync(curPath).isDirectory()){
              this.delDir(curPath); //递归删除文件夹
          } else {
              fs.unlinkSync(curPath); //删除文件
          }
        });
        fs.rmdirSync(path);
    	}
    },
    // 创建流程工作空间, 及流程xml文件
    createProcessSpace(directory, processId) {
    	let processSpacePath = this.$workspacePath + 'liucheng_' + directory + '/';
    	if(!fs.existsSync(processSpacePath)) {
    		fs.mkdirSync(processSpacePath);
    	}
    	let data = this.$x2js.js2xml(this.processArray[processId]);
    	fs.writeFile(processSpacePath + processId + '.xml',
    		'<config>' + data + '</config>', function(err) {
	    		if(err) {
	    			console.error(err);
	    			return console.error("创建流程数据文件失败");
	    		}
    	});
    },
    // 创建流程树文件
    createTreeConfig(jsonData, dataId, fileName) {
    	let xmlHeader = '<?xml version="1.0" encoding="UTF-8"?>';
//  	let fileName = root + 'process_tree.xml';
    	let arrayXml;
    	// 流程树xml文件存在, 新增流程时为追加操作
  		if(fs.existsSync(fileName)) {
  			let that = this;
  			fs.readFile(fileName, function(err, data) {
  				if (err) {
  					console.error(err)
				    return console.error("没找到流程树配置文件");
				  }
  				let arrayJson = that.$x2js.xml2js(data.toString());
  				// 流程删完后, arrayJson.config读取结果是空串, 将其改为空对象
  				if((typeof arrayJson.config) === 'string') {
  					arrayJson.config = {};
  				}
  				arrayJson.config[dataId] = jsonData[dataId]
  				arrayXml = xmlHeader + that.$newLine + that.$x2js.js2xml(arrayJson);
  				fs.writeFile(fileName, arrayXml, function(err) {
				   	if (err) {
				   		console.error(err)
			       	return console.error("创建流程树配置文件失败");
				   	}
					})
  			})
  		}
  		else {
  			let tmp = this.$x2js.js2xml(jsonData);
  			arrayXml = xmlHeader + this.$newLine + "<config>" + tmp + "</config>";
  			fs.writeFile(fileName, arrayXml, function(err) {
			   	if (err) {
		       	return console.error(err);
			   	}
				})
  		}
    },
    /*****************   数据源鼠标右击事件相关方法   *****************/
	  datasourceMouseDown(index) {
	  	this.datasourceSelected(index)
	  },
   	datasourceSelected(index) {
			this.activeIndex2 = index
		},
    showMenu2 () {
      event.preventDefault()
      var x = event.clientX
      var y = event.clientY
      // Get the current location
      this.contextMenuData.axios = {
        x, y
      }
    },
    addDataSource () {
    	this.dialogActionType = true;
    	this.componentDialogVisible = true;
    },
    // 列表右键菜单
    removeDatasource() {
    	let id = this.activeIndex2;
    	console.log(id)
			let xmlHeader = '<?xml version="1.0" encoding="UTF-8"?>';
    	let arrayXml;
    	let that = this;
    	let fileName = this.$workspacePath + 'datasource_tree.xml';
    	// 从流程树文件中删除当前流程的节点
			fs.readFile(fileName, function(err, data) {
				if (err) {
					console.error(err)
			    return console.error("没找到数据源树配置文件");
			  }
				let arrayJson = that.$x2js.xml2js(data.toString())
				delete arrayJson.config[id]
				arrayXml = xmlHeader + that.$newLine + that.$x2js.js2xml(arrayJson);
				fs.writeFile(fileName, arrayXml, function(err) {
			   	if (err) {
			   		console.error(err)
		       	return console.error("创建数据源树配置文件失败");
			   	}
				})
			})
			// 删除当前流程工作目录及其下所有文件
			this.REMOVE_DATASOURCE_TREE(id);
    },
    showDatasourceItem () {
      event.preventDefault()
      var x = event.clientX
      var y = event.clientY
      // Get the current location
      this.childDatasourceMenuData.axios = {
        x, y
      }
    },
   	visibleChange(val) {
   		var action = val.action;
   		if(action === 'confirm') {
   			this.createTreeConfig(this.datasourceTree, val.datasourceId, this.$workspacePath + 'datasource_tree.xml');
   		}
      this.componentDialogVisible = val.visibleChange
	  },
	  openDatasource(id) {
    	this.dataSourceData.id = id;
    	this.dialogActionType = false
    	this.componentDialogVisible = true
    },
    /***************** 数据源鼠标右击事件相关方法结束 *****************/
   	doClose(val) {
   		this.componentDialogVisible = val
   	}
  },
  mounted() {
  	var that = this;
  	// 刚打开设计器时, 读工作空间的流程树文件
  	if(this.processArray.length == 0) {
			var processTreePath = this.$workspacePath + 'process_tree.xml';
			if(!fs.existsSync(processTreePath)) return;
			fs.readFile(processTreePath, function(err, data) {
				if(err) {
					return console.error(err);
				}
				let processTreeJson = that.$x2js.xml2js(data.toString());
				that.UPDATE_REPLACE_PROCESS_TREE(processTreeJson.config);
			})
  	}
  	// 读工作空间的数据源树文件
  	if(this.datasourceTree.length == 0) {
  		var datasourceTreePath = this.$workspacePath + 'datasource_tree.xml';
			if(!fs.existsSync(datasourceTreePath)) return;
			fs.readFile(datasourceTreePath, function(err, data) {
				if(err) {
					return console.error(err);
				}
				let datasourceTreeJson = that.$x2js.xml2js(data.toString())
				that.UPDATE_REPLACE_DATASOURCE_TREE(datasourceTreeJson.config);
			})
  	}
  }
}
</script>

<style lang="scss">
.flow-tree {
  width: 208px;
  position: absolute;
  top: 30px;
  bottom: 0;
  overflow: auto;
  background: whiteSmoke;
  box-shadow: -1px 0px 5px #bbb inset;
  border-right: 1px solid #ddd;
  .flt-list-svg {
    width: 36px;
    height: 36px;
    display: block;
    position: relative;
    overflow: hidden;
    cursor: move;
    left: 2px;
    top: 2px;
  }
  .flt-container {
  	/* 右键菜单样式  */
  	.vue-contextmenu-listWrapper {
			.context-menu-list{
				width: 50px;
				height: 24px;
				button {
					background: #eee;
					text-align: center;
					span {
						float: none;
					}
					span:hover {
						color: #000000;
					}
				}
				button:hover {
					background: #eee;
					color: #000000;
				}
			}
		}
    .flt-list-tit {
      color: #333;
      background: #eee;
      padding: 6px 0px 6px 14px;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
      line-height: 1.4em;
      font-size: 9pt;
      display: flex;
      align-items: center;
      cursor: default;
    }
    ul {
      margin-right: 3px;
    }
    .flt-icon {
      margin-right: 8px;
    }
    .flt-shape-list {
      display: flex;
      flex-wrap: wrap;
      background: #f5f5f5;
      .flt-item-box {
        display: flex;
        padding: 1px;
        flex-wrap: wrap;
        align-items: center;
        cursor: move;
        .flt-list-item {
          width: 36px;
          height: 36px;
          overflow: hidden;
          cursor: move;
          background: none;
          margin-left: 10px;
        }
        .li-list-item {
          width: 204px;
          height: 28px;
          line-height: 28px;
          overflow: hidden;
          cursor: default;
          background: none;
          padding-left: 10px;
          .el-icon-caret-right:before {
				    content: "\E606";
				    color: #595A5A;
					}
        }
        .active{
					border: solid 1px #D3D3D3;
					border-radius:5px;
				}
      }
    }
  }
  .flt-input-svg {
    touch-action: none;
    cursor: pointer;
    border: 3px solid transparent;
  }

  .flt-input-svg-box {
    border-radius: 6px;
    border: 3px dotted lightgray;
    text-align: center;
    padding: 8px;
    color: rgb(179, 179, 179);
  }

  .flt-search {
    box-sizing: border-box;
    overflow: hidden;
    width: 100%;
    padding: 14px 15px 0px;
    .flt-input-wrap {
      white-space: nowrap;
      text-overflow: clip;
      padding-bottom: 8px;
      cursor: default;
    }
    .flt-input {
      font-size: 12px;
      overflow: hidden;
      box-sizing: border-box;
      border: 1px solid rgb(213, 213, 213);
      border-radius: 4px;
      width: 100%;
      outline: none;
      padding: 6px 20px 6px 6px;
    }
    .flt-search-icon {
      position: absolute;
      top: 23px;
      right: 25px;
    }
  }
}
</style>
