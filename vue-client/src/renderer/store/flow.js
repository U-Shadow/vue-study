import Vue from 'vue'
//import { cloneDeep } from 'lodash/fp'

export const SEL_NODETYPE = 'SEL_NODETYPE'
export const SEL_LINETYPE = 'SEL_LINETYPE'
// 新增(更新(新增或着更新))节点
export const UPDATE_NODE = 'UPDATE_NODE'
export const UPDATE_HOVER_NODE = 'UPDATE_HOVER_NODE'
// 新增(更新(新增或着更新))连线
export const UPDATE_LINE = 'UPDATE_LINE'
export const UPDATE_SHADOW_LINE = 'UPDATE_SHADOW_LINE'
export const UPDATE_DRAWSTYLE = 'UPDATE_DRAWSTYLE'
// 删除节点
export const REMOVE_NODE = 'REMOVE_NODE'
// 选择节点
export const SELECTED_NODE = 'SELECTED_NODE'
// 选中的连线
export const SELECTED_LINE = 'SELECTED_LINE'
// 更新(新增或着更新)组件数据
export const UPDATE_COMPONENT_DATA = 'UPDATE_COMPONENT_DATA'
// 更新(新增或着更新)流程
export const UPDATE_PROCESS = 'UPDATE_PROCESS'
// 更新(新增或着更新)当前流程的状态
export const UPDATE_PROCESS_STATUS = 'UPDATE_PROCESS_STATUS'
// 当前流程ID
export const CURRENT_PROCESS = 'CURRENT_PROCESS'
// 切换流程
export const CHANGE_PROCESS = 'CHANGE_PROCESS'
// 更新流程的输入组件的数目
export const UPDATE_INPUTNODENUM = 'UPDATE_INPUTNODENUM'
// 更新流程的非输入组件的数目
export const UPDATE_ISNOTINPUTNODENUM = 'UPDATE_ISNOTINPUTNODENUM'
// 取消选中
export const CANAEL_SELECTED = 'CANAEL_SELECTED'

const store = {
  namespaced: true,
  state: {
  	// 流程中输入节点的数量(每个流程必须有且只有一个输入节点)
  	inputNodeNum: 0,
  	// 流程中非输入节点的数量(每个流程中至少有一个非输入节点)
  	isNotInputNodeNum: 0,
  	// 流程当前状态, save: 保存; run: 运行中; edit: 修改
  	processStatus: '',
  	// 流程信息
  	/*processInfo: {
  		processes: [],
  		keys: {},	// 流程中节点对应变量名称对象, 节点id对应变量名称(如果一个节点会对应多个变量名称,节点后面一次加_a、_b...)
  		v_k: {}	// 流程中变量名称对应节点对象, 用于判断填写的变量名称是否存在
  	},*/
  	// 保存当前当前流程的ID, 名称以及流程中生成的变量名称和变量名称对应的流程ID
  	processInfo: {},
  	// 当前流程ID
  	currentProcess: '',
  	// 自定义组件保存数据
    componentData: {},
    // 当前选中节点类型
    selNodeType: '',
    // 节点数据
    nodeData: {},
    hoverNodeData: {
      id: ''
    },
    // 连线数据
    lineData: null,
    shadowLineData: null,
    // 画布样式
    drawStyle: {
      zoomRate: 1 // 全局缩放比例
    },
    selLineType: 'StraightLine',
    selectedNode: '', // 选中节点
    selectedLine: '' // 选中连线
  },
  mutations: {
  	[UPDATE_INPUTNODENUM](state) {
  		state.inputNodeNum++;
  	},
  	[UPDATE_ISNOTINPUTNODENUM](state) {
  		state.isNotInputNodeNum++;
  	},
  	[CHANGE_PROCESS](state, value) {
  		this.state.flow = value.flowData
  	},
  	[CURRENT_PROCESS](state, value = '') {
  		state.currentProcess = value
  	},
  	[UPDATE_PROCESS_STATUS](state, value = '') {
  		state.processStatus = value
  	},
  	// 流程信息
  	[UPDATE_PROCESS](state, value) {
  		state.processInfo = {
  			...state.processInfo,
        ...value
  		}
  		this.state.flow.processStatus = 'edit';
  		this.commit('processStore/UPDATE_PROCESSARRAY', {
      	[state.currentProcess]: state
      });
      this.commit('processStore/UPDATE_PROCESS_TREE', {
      	[state.currentProcess]: value[state.currentProcess]
      });
  	},
  	// 更新(新增或着更新)自定义组件保存数据
  	[UPDATE_COMPONENT_DATA](state, value) {
  		state.componentData = {
  			...state.componentData,
        ...value
  		}
  		this.state.flow.processStatus = 'edit';
  		this.commit('processStore/UPDATE_PROCESSARRAY', {
      	[state.currentProcess]: state
      });
  	},
    // 选中节点类型
    [SEL_NODETYPE] (state, value = '') {
      state.selNodeType = value;
      this.commit('processStore/UPDATE_PROCESSARRAY', {
      	[state.currentProcess]: state
      });
    },
    // 选中线条类型
    [SEL_LINETYPE] (state, value = '') {
      state.selLineType = value
    },
    // 修改画布大小位置等
    [UPDATE_DRAWSTYLE] (state, value) {
      state.drawStyle = {
        ...state.drawStyle,
        zoomRate: value.zoomRate
      }
      this.commit('processStore/UPDATE_PROCESSARRAY', {
      	[state.currentProcess]: state
      });
    },
    [UPDATE_NODE] (state, value) {
      state.nodeData = {
        ...state.nodeData,
        ...value
      }
      this.state.flow.processStatus = 'edit'
      this.commit('processStore/UPDATE_PROCESSARRAY', {
      	[state.currentProcess]: state
      });
    },
    [UPDATE_HOVER_NODE] (state, value) {
      state.hoverNodeData = {
        ...state.hoverNodeData,
        ...value
      }
      this.commit('processStore/UPDATE_PROCESSARRAY', {
      	[state.currentProcess]: state
      });
    },
    [UPDATE_LINE] (state, value) {
      state.lineData = {
        ...state.lineData,
        ...value
      }
      this.state.flow.processStatus = 'edit';
      this.commit('processStore/UPDATE_PROCESSARRAY', {
      	[state.currentProcess]: state
      });
    },
    [UPDATE_SHADOW_LINE] (state, value) {
      state.shadowLineData = value
    },
    // 删除节点
    [REMOVE_NODE] (state, vlaue) {
    	// 删除节点
      if (state.selectedNode !== '') {
        let nodeId = state.selectedNode;
        // 根据删除的节点操作流程中输入节点和非输入节点的数目
        let isInputNode = state.nodeData[nodeId].isInputNode;
        if(isInputNode == 'Y') {
        	state.inputNodeNum--;
        }
        else {
        	state.isNotInputNodeNum--;
        }
        // 删除与节点相关的连线数据
        let lines = state.nodeData[nodeId].lines;
        if (lines) {
          for (var i = 0; i < lines.length; i++) {
          	let lineId = lines[i];
          	let startId = state.lineData[lineId].startId;
          	// 清楚上个节点的下个节点信息
          	this.state.flow.nodeData[startId].nextNodeId = '';
            Vue.delete(state.lineData, lineId);
          }
        }
        // 删除节点
        Vue.delete(state.nodeData, nodeId);
        // 删除该节点的组件数据
        if(state.componentData[nodeId]) {
        	Vue.delete(state.componentData, nodeId)
        }
    		state.selectedNode = ''
      }
      // 删除连线
      if (state.selectedLine !== '') {
      	let lineId = state.selectedLine;
      	let startId = state.lineData[lineId].startId;
      	// 清除上个节点的下个节点信息
      	this.state.flow.nodeData[startId].nextNodeId = '';
        Vue.delete(state.lineData, lineId);
    		state.selectedLine = ''
      }
    	// 修改历史数据
      this.commit('processStore/UPDATE_HISTORY_DATA');
      this.state.flow.processStatus = 'edit';
      this.commit('processStore/UPDATE_PROCESSARRAY', {
      	[state.currentProcess]: state
      });
    },
    // 选中的节点
    [SELECTED_NODE] (state, value) {
    	state.selectedLine = '';	// 取消选中的连线
      state.selectedNode = value.nodeId;
      this.commit('processStore/UPDATE_PROCESSARRAY', {
      	[state.currentProcess]: state
      });
    },
    // 选中的连线
    [SELECTED_LINE] (state, value) {
    	state.selectedNode = '';	// 取消选中的节点
      state.selectedLine = value.lineId;
      this.commit('processStore/UPDATE_PROCESSARRAY', {
      	[state.currentProcess]: state
      });
    },
    [CANAEL_SELECTED] (state, value) {
    	state.selectedNode = '';	// 取消选中的节点
    	state.selectedLine = '';	// 取消选中的连线
    }
  },
  actions: {
  }
}

export default store
