import { cloneDeep } from 'lodash/fp'
import Vue from 'vue'
import flow from './flow'

export const UPDATE_PROCESSARRAY = 'UPDATE_PROCESSARRAY'
export const UPDATE_PROCESS_TREE = 'UPDATE_PROCESS_TREE'
export const UPDATE_REPLACE_PROCESS_TREE = 'UPDATE_REPLACE_PROCESS_TREE'
export const REMOVE_PROCESS_TREE = 'REMOVE_PROCESS_TREE'
export const UPDATE_DATASOURCE_TREE = 'UPDATE_DATASOURCE_TREE'
export const UPDATE_REPLACE_DATASOURCE_TREE = 'UPDATE_REPLACE_DATASOURCE_TREE'
export const REMOVE_DATASOURCE_TREE = 'REMOVE_DATASOURCE_TREE'
export const UPDATE_QUEUE = 'UPDATE_QUEUE'
export const REMOVE_QUEUE = 'REMOVE_QUEUE'
// 撤销
export const UNDO = 'UNDO'
// 重做
export const REDO = 'REDO'
// 更新(新增或者更新)流程历史数据
export const UPDATE_HISTORY_DATA = 'UPDATE_HISTORY_DATA'
// 历史数据版本
export const UPDATE_HISTORYINDEX = 'UPDATE_HISTORYINDEX'
// 重置flow store数据
export const REST = 'REST'
// 初始化历史数据
export const INIT_HISTORY = 'INIT_HISTORY'

const processStore = {
	namespaced: true,
	state: {
		// 流程历史数据, 流程id对应历史数据
		/*p_historys: {
			[pid]: {
				// 历史数据版本
				historyIndex: 0,
				// 历史数据
				historyData: [],
		    isReDo: false, // 是否可以重做
		    isUnDo: true // 是否可以撤销
			}
		}*/
		p_historys: {},
		// 用于页面计算属性监听数据改变, 从而计算撤销重做是否可用
		p_historys_update: 0,
		datasourceTree: [],
		processTree: [],
		processArray: [],	// 流程数据, 流程id对应该流程的数据
		// 队列数据
  	queue: []
	},
	mutations: {
    // 重置
    [REST] (state, value = '') {
    	// 此处的state注册成flow
    	this.state.flow = cloneDeep(defaultFlowState);
    	this.state.flow.currentProcess = value
    	this.state.flow.processStatus = 'edit';
    },
		// 撤销
    [UNDO] (state, value = 0) {
    	let current_process = this.state.flow.currentProcess;
    	// 历史数据版本
    	var p_historyIndex = state.p_historys[current_process].historyIndex;
      if (p_historyIndex > 0) {
      	// 当前版本
        let p_currentIndex = state.p_historys[current_process].currentIndex;
        // 复制用cloneDeep, 否则为引用传递, 后续会修改该值
        this.state.flow = cloneDeep(state.p_historys[current_process].historyData[p_currentIndex - 1]);
        this.state.flow.shadowLineData = null;
	      state.p_historys[current_process].currentIndex--;
	      this.state.flow.processStatus = 'edit';
	      this.commit('processStore/UPDATE_PROCESSARRAY', {
	      	[current_process]: this.state.flow
	      });
      }
    },
    // 重做
    [REDO] (state, value = 0) {
    	let current_process = this.state.flow.currentProcess;
    	// 历史数据版本
    	var p_historyIndex = state.p_historys[current_process].historyIndex;
    	let p_currentIndex = state.p_historys[current_process].currentIndex;
      if (p_currentIndex < p_historyIndex-1) {
      	// 当前版本
        // 复制用cloneDeep, 否则为引用传递, 后续会修改该值
        this.state.flow = cloneDeep(state.p_historys[current_process].historyData[p_currentIndex + 1]);
        this.state.flow.shadowLineData = null;
	      state.p_historys[current_process].currentIndex++;
	      this.state.flow.processStatus = 'edit';
	      this.commit('processStore/UPDATE_PROCESSARRAY', {
	      	[current_process]: this.state.flow
	      });
      }
    },
    // 初始化历史数据
    [INIT_HISTORY] (state, value) {
			state.p_historys[value] = {
				isUnDo: false,
				historyData: [],	// 历史总数
				historyIndex: 0,	// 历史数据版本总数
				currentIndex: 0	// 当前数据版本
			}
			state.p_historys[value].historyData.push(cloneDeep(this.state.flow));
		},
		// 更新(新增或着更新)历史数据
    [UPDATE_HISTORY_DATA] (state, value) {
    	let current_process = this.state.flow.currentProcess;
			let p_historyData = cloneDeep(state.p_historys[current_process].historyData);
      state.p_historys[current_process].historyIndex = p_historyData.length;
      state.p_historys[current_process].currentIndex++;
    	// 前一个状态的数据
    	let previousData = cloneDeep(this.state.flow);
    	let p_historyIndex = state.p_historys[current_process].historyIndex;
    	let p_currentIndex = state.p_historys[current_process].currentIndex;
    	if(p_currentIndex < p_historyIndex) {
    		let newHistoryData = p_historyData.slice(0, p_currentIndex);
    		newHistoryData.push(previousData);
    		state.p_historys[current_process].historyData = newHistoryData;
    		state.p_historys[current_process].historyIndex = newHistoryData.length;
    	}
    	else {
    		state.p_historys[current_process].historyData.push(previousData);
    		state.p_historys[current_process].historyIndex = state.p_historys[current_process].historyData.length;
    	}
    	state.p_historys[current_process].isUnDo = true;
    	state.p_historys_update++;
    	this.commit('processStore/UPDATE_PROCESSARRAY', {
      	[current_process]: this.state.flow
      });
    },
    [UPDATE_HISTORYINDEX] (state, value) {
    	let current_process = this.state.flow.currentProcess;
			let p_historyData = cloneDeep(state.p_historys[current_process].historyData);
      state.p_historys[current_process].historyIndex = p_historyData.length;
      state.p_historys[current_process].currentIndex++;
      this.commit('processStore/UPDATE_PROCESSARRAY', {
      	[current_process]: this.state.flow
      });
    },
		[UPDATE_PROCESSARRAY](state, value) {
			state.processArray = {
				...state.processArray,
				...value
			}
		},
		[UPDATE_PROCESS_TREE](state, value) {
			state.processTree = {
				...state.processTree,
				...value
			}
		},
		[UPDATE_REPLACE_PROCESS_TREE](state, value) {
			state.processTree = value
		},
		// 删除流程
  	[REMOVE_PROCESS_TREE](state, value) {
  		Vue.delete(state.processTree, value)
  	},
		[UPDATE_DATASOURCE_TREE](state, value) {
			state.datasourceTree = {
				...state.datasourceTree,
				...value
			}
		},
		[UPDATE_REPLACE_DATASOURCE_TREE](state, value) {
			state.datasourceTree = value
		},
		// 删除数据源
  	[REMOVE_DATASOURCE_TREE](state, value) {
  		Vue.delete(state.datasourceTree, value)
  	},
  	// 更新(新增或着更新)队列
  	[UPDATE_QUEUE](state, value) {
  		state.queue = {
  			...state.queue,
        ...value
  		}
  	},
  	// 删除队列
  	[REMOVE_QUEUE](state, value) {
  		Vue.delete(state.queue, value)
  	}
	},
	actions: {
		updateProcessData({commit}, value) {
			commit('UPDATE_PROCESSARRAY', value)
		}
	}
}

// 保存流程的初始状态
const defaultFlowState = cloneDeep(flow.state)

export default processStore
