<template>
  <div id="flowLeftTool" class="flow-left-tool" @click="cancelNodeSelected">
    <div class="flt-container">
      <div class="flt-search">
        <div class="flt-input-wrap">
          <input placeholder="搜索形状" type="text" class="flt-input" v-model="searchCont">
          <icon name="sousuo" :size="14" class="flt-search-icon"></icon>
        </div>
      </div>
      <!--<div style="margin:10px">
        <div class="flt-input-svg" title="添加SVG" @drop.stop.prevent="dropHandle" @dragover.stop.prevent="dragoverHandle">
          <div class="flt-input-svg-box">添加SVG</div>
        </div>
      </div>-->
      <ul>
        <!-- 队列  -->
        <!--<li class="flt-tool-list">
          <div class="flt-list-tit" @click.stop="queueItems.show = !queueItems.show"  @contextmenu="showMenu2">
          	<vue-context-menu :contextMenuData="contextMenuData2" @addQueue="addQueue">
    				</vue-context-menu>
            <icon :name="queueItems.show?'down-arrow':'up-arrow'" :size="8" class="flt-icon"></icon>
            <span>{{queueItems.title}}</span>
          </div>
          <div class="flt-shape-list" v-show="queueItems.show">
            <div class="flt-item-box">
            	<div :class="activeIndex2 === index ? 'li-list-item active': 'li-list-item'" 
            		draggable="true" @click="selected(index)"
            		v-for="(item,index) in queue" :key="index"
            		@dblclick="openQueue(index)" @contextmenu="showQueueItem">
          			<icon name="right-arrow" :size="8" style="margin-left: 8px; margin-bottom: 2px;"></icon>
          			<span>{{item.data.queueName}}</span>
	              <vue-context-menu :contextMenuData="queueMenuData" 
	              	@addQueue="addQueue" @removeQueue="removeQueue(index)">
	    					</vue-context-menu>
              </div>
            </div>
          </div>
        </li>-->
        <li class="flt-tool-list" v-for="(tool,index) in searchToolItems" :key="index">
          <div class="flt-list-tit" @click.stop="tool.show = !tool.show; cancelNodeSelected()">
            <icon :name="tool.show?'down-arrow':'up-arrow'" :size="8" class="flt-icon"></icon>
            <span>{{tool.title}}</span>
          </div>
          <div class="flt-shape-list" v-show="tool.show">
            <div class="flt-item-box">
              <template v-if="tool.title==='自定义'">
                <div class="flt-list-item" draggable="true" v-for="(item,index) in userImages" :key="index" :title="item.title"
                	@mousedown="selNode('SvgImage', item.title)" @dragstart="dragstart(item)">
                  <svg class="flt-list-svg">
                    <svg-image :imgSrc="item"></svg-image>
                  </svg>
                </div>
              </template>
              <template v-else>
                <div class="flt-list-item" draggable="true" v-for="(item,index) in tool.listData" :key="index" :title="item.title"
                	@dragstart="selNode(item)" @dragend="nodeDragEnd"	>
                  <icon :name="item.type" :size="30" transform="translate(0.5,0.5)" />
                </div>
              </template>
            </div>
          </div>
        </li>
      </ul>
    </div>
    
    <queue :queueDialogTitle="queueDialogTitle" :queueActionType="queueActionType" :queueData="queueData"
    	:queueDialogVisible="queueDialogVisible" @visibleChange2="visibleChange2" ></queue>
  </div>
</template>

<script>
import { mapMutations, mapState } from 'vuex'
import dataSource from '@/components/dialog/DataSourceComponent'
import queue from '@/components/dialog/QueueComponent'

export default {
  name: 'flow-left-tool',
  components: {
  	dataSource,
  	queue
  },
  data () {
    return {
      activeIndex: '',
      /* 新增数据源定义数据  begin */
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
      datasourceMenuData: {
        menuName: 'datasourceItem',
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
      },
      /* 新增数据源定义数据  end */
     	/****************************************************************************************************/
     	activeIndex2: '',
     	/******* 新增数据源定义数据  begin *******/
      // contextmenu data (菜单数据)
      contextMenuData2: {
      // the contextmenu name(@1.4.1 updated)
        menuName: 'queueItems',
      // The coordinates of the display(菜单显示的位置)
        axios: {
          x: null,
          y: null
        },
      // Menu options (菜单选项)
        menulists: [
          {
            fnHandler: 'addQueue', // Binding events(绑定事件)
            icoName: '', // icon (icon图标 )
            btnName: '新增' // The name of the menu option (菜单名称)
          }
        ]
      },
      queueMenuData: {
        menuName: 'queueItem',
      	// The coordinates of the display(菜单显示的位置)
        axios: {
          x: null,
          y: null
        },
      	// Menu options (菜单选项)
        menulists: [
          {
            fnHandler: 'addQueue', // Binding events(绑定事件)
            icoName: '', // icon (icon图标 )
            btnName: '新增' // The name of the menu option (菜单名称)
          },
          {
            fnHandler: 'removeQueue', // Binding events(绑定事件)
            icoName: '', // icon (icon图标 )
            btnName: '删除' // The name of the menu option (菜单名称)
          }
        ]
      },
      queueDialogTitle: '新增队列',
      queueDialogVisible: false,
      queueData: {},
      queueActionType: true,		// 是否新增队列true, false
      queueItems: {
        title: '队列',
        show: true
      },
      /* 新增数据源定义数据  end */
      selItem: undefined,
      searchCont: '',
      toolItems: [
        {
          title: '组件',
          listData: [
            {
              isInputNode: 'N',	// 是否是输入节点, 用于控制流程中输入节点和非输入节点的验证
              type: 'http',
              title: 'http调用'
            },
            {
              isInputNode: 'Y',
              type: 'jetty',
              title: 'jetty服务'
            },
            {
              isInputNode: 'N',
              type: 'dataQuery',
              title: '数据查询'
            },
            {
            	isInputNode: 'N',
              type: 'fileInput',
              title: '文件读取'
            },
            {
            	isInputNode: 'N',
              type: 'fileOutput',
              title: '文件输出'
            },
            {
            	isInputNode: 'N',
              type: 'sqlExcute',
              title: 'sql执行'
            },
            {
            	isInputNode: 'N',
              type: 'JMSOutput',
              title: 'JMS消息输出'
            },
            {
            	isInputNode: 'Y',
              type: 'JMSInput',
              title: 'JMS消息输入'
            },
            {
            	isInputNode: 'N',
              type: 'JMSFetch',
              title: 'JMS获取'
            }
          ],
          show: true
        }/*,
        {
          title: '物联网',
          listData: [
            {
              type: 'duidaoyi',
              title: '对刀仪机器人'
            },
            {
              type: 'chechuangjiqiren',
              title: '机器人'
            },
            {
              type: 'gongchang1',
              title: '工厂'
            },
            {
              type: 'gongchang',
              title: '折弯机'
            },
            {
              type: 'qichexuanfu',
              title: '汽车悬浮'
            },
            {
              type: 'dianji',
              title: '电机'
            },
            {
              type: 'qichemada',
              title: '汽车发动机'
            }
          ],
          show: true
        },
        {
          title: '网络图形',
          listData: [
            {
              type: 'network',
              title: '网络'
            },
            {
              type: 'virtual-device',
              title: '虚拟设备'
            },
            {
              type: 'middleware',
              title: '中间件'
            },
            {
              type: 'monitor',
              title: '显示器'
            },
            {
              type: 'dome-camera',
              title: '球形摄像机'
            },
            {
              type: 'gun-camera',
              title: '枪式摄像机'
            },
            {
              type: 'printer',
              title: '打印机'
            },
            {
              type: 'network-server',
              title: '网络服务器'
            },
            {
              type: 'host-hardware',
              title: '主机硬件'
            },
            {
              type: 'building',
              title: '建筑'
            },
            {
              type: 'business-services',
              title: '业务服务'
            },
            {
              type: 'database',
              title: '数据库'
            },
            {
              type: 'router',
              title: '路由器'
            },
            {
              type: 'switch',
              title: '交换机'
            },
            {
              type: 'hubs',
              title: '集线器'
            },
            {
              type: 'layer-three-switch',
              title: '三层交换机'
            },
            {
              type: 'unknown-device',
              title: '未知设备'
            },
            {
              type: 'server',
              title: '服务器'
            },
            {
              type: 'firewall-fire',
              title: '防火墙'
            },
            {
              type: 'standard-service',
              title: '标准服务器'
            }
          ],
          show: true
        },
        {
          title: '自定义',
          listData: [
            {
              type: 'start',
              title: '开始'
            }
          ],
          show: true
        }*/
      ],
      svgStyle: {},
      userImages: []
    }
  },
  computed: {
    ...mapState('flow', ['inDrawArea', 'selNodeType', 'queue']),
    searchToolItems () {
      if (this.searchCont === '') {
        return this.toolItems
      } else {
        let deepCopy = (s, t = {}) => {
          for (var i in s) {
            if (typeof s[i] === 'object') {
              t[i] = s[i].constructor === Array ? [] : {}
              deepCopy(s[i], t[i])
            } else {
              t[i] = s[i]
            }
          }
          return t
        }
        let list = this.toolItems.map(item => {
          return deepCopy(item)
        })
        list = list.filter(item => {
          if (item.title === '自定义') {
            return true
          }
          let listData = item.listData.filter(item => {
            if (item.title.indexOf(this.searchCont) >= 0) {
              return true
            }
          })
          if (listData.length !== 0) {
            item.listData = listData
            return true
          }
        })
        return list
      }
    }
  },
  methods: {
    ...mapMutations('flow', ['SEL_NODETYPE', 'SET_DRAGGING', 'SET_INDRAWAREA', 'SELECTED_NODE', 'REMOVE_DATASOURCE',
    'REMOVE_QUEUE']),
    selected(index) {
			this.activeIndex = index
		},
    openDatasource(id) {
    	this.dataSourceData.id = id
    	this.dialogActionType = false
    	this.componentDialogVisible = true
    },
    /*****************   数据源鼠标右击事件相关方法   *****************/
    showMenu () {
      event.preventDefault()
      var x = event.clientX
      var y = event.clientY
      // Get the current location
      this.contextMenuData.axios = {
        x, y
      }
    },
    addDataSource () {
    	let id = 'dataSource-' + new Date().getTime()
    	this.dataSourceData.id = id
    	this.componentDialogVisible = true
    },
    // 列表右键菜单
    removeDatasource(id) {
    	this.REMOVE_DATASOURCE(id)
    },
    showDatasourceItem () {
      event.preventDefault()
      var x = event.clientX
      var y = event.clientY
      // Get the current location
      this.datasourceMenuData.axios = {
        x, y
      }
    },
    /***************** 数据源鼠标右击事件相关方法结束 *****************/
   	queueSelected(index) {
			this.activeIndex2 = index
		},
   	openQueue(id) {
    	this.queueData.id = id
    	this.queueActionType = false
    	this.queueDialogVisible = true
    },
    /***************** 队列鼠标右击事件相关方法   *****************/
    showMenu2 () {
      event.preventDefault()
      var x = event.clientX
      var y = event.clientY
      // Get the current location
      this.contextMenuData2.axios = {
        x, y
      }
    },
    addQueue () {
    	let id = 'queue-' + new Date().getTime()
    	this.queueData.id = id
    	this.queueDialogVisible = true
    },
    // 列表右键菜单
    removeQueue(id) {
    	this.REMOVE_QUEUE(id)
    },
    showQueueItem () {
      event.preventDefault()
      var x = event.clientX
      var y = event.clientY
      // Get the current location
      this.queueMenuData.axios = {
        x, y
      }
    },
    /***************** 队列鼠标右击事件相关方法结束 *****************/
   	visibleChange(val) {
      this.componentDialogVisible = val
	  },
	  visibleChange2(val) {
      this.queueDialogVisible = val
	  },
    // 取消选中的节点
    cancelNodeSelected () {
    	let nodeId = ''
    	this.SELECTED_NODE({
        nodeId
      })
    },
    selNode (item) {
      this.SEL_NODETYPE(item.type);
      event.dataTransfer.setData('Text', 'add');
      event.dataTransfer.setData('title', item.title);
      event.dataTransfer.setData('isInputNode', item.isInputNode);
    },
    nodeDragEnd () {
      if (this.selNodeType) {
        this.SEL_NODETYPE('')
      }
    },
    dropHandle (e) {
      let reader = new FileReader()
      let file = e.dataTransfer.files[0]
      reader.onload = e => {
        this.userImages.push(e.target.result)
      }
      reader.readAsDataURL(file)
    },
    dragoverHandle () {},
    dragstart (imgSrc) {
      event.dataTransfer.setData('URL', imgSrc)
      event.dataTransfer.setData('Text', 'add')
    }
  }
}
</script>

<style lang="scss">
.flow-left-tool {
  width: 208px;
  position: absolute;
  top: 66px;
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
