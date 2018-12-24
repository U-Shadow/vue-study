<template>
	<div id="">
	  <div id="flowMainCont" :class="isShowTree ? 'flow-main-cont isShowTree' : 'flow-main-cont'"
	  	@click="cancelNodeSelected">
	    <div id="drawContainer"
	      class="draw-wrap">
	      <flow-crumbs :isShowTree="isShowTree"></flow-crumbs>
	      <div id="draw" class="main-bg"
	        :style="{transform:`scale(${drawStyle.zoomRate})`,transformOrigin:`left top 0px`}"
	        @drop.prevent="dropHandle" @dragover.stop.prevent @mousewheel.alt.prevent="wheelHandle">
	        <flow-node-draw @componentDialog="componentDialog"></flow-node-draw>
	        <flow-line-draw></flow-line-draw>
	      </div>
	    </div>
	  </div>
	  
  	<custom-components :nodeData="selectedNode" :dialogActionType="dialogActionType"
			:componentDialogTitle="componentDialogTitle" :componentDialogVisible="componentDialogVisible" 
			@visibleChange="visibleChange" ></custom-components>
	  	
	</div>
</template>

<script>
import { mapState, mapMutations } from 'vuex'
import FlowNodeDraw from './FlowNodeDraw'
import FlowLineDraw from './FlowLineDraw'
import FlowCrumbs from './FlowCrumbs'

export default {
  name: 'flowMainDraw',
  data () {
    return {
    	selectedNode: {},
    	dialogActionType: '',		// 弹出框按钮确认类型(确定/取消)
    	componentDialogVisible: false,
    	componentDialogTitle: '',
      zoomType: 'zoomIn'
    }
  },
  props: {
  	isShowTree: Boolean
  },
  components: {
    FlowNodeDraw,
    FlowLineDraw,
    FlowCrumbs
  },
  watch: {
  },
  computed: {
    ...mapState('flow', [
      'nodeData',
      'selNodeType',
      'lineData',
      'selLineType',
      'drawStyle',
      'hoverNodeData',
      'inputNodeNum',
      'isNotInputNodeNum'
    ])
  },
  methods: {
  	...mapMutations('flow', ['UPDATE_NODE', 'UPDATE_DRAWSTYLE', 'UPDATE_LINE', 'CANAEL_SELECTED',
  		'UPDATE_INPUTNODENUM', 'UPDATE_ISNOTINPUTNODENUM']),
  	...mapMutations('processStore', ['UPDATE_HISTORY_DATA']),
    visibleChange(val) {
      this.componentDialogVisible = val
	  },
  	// 确定
  	confirm() {
  		this.componentDialogVisible = false
  		this.dialogActionType = true
  	},
  	// 取消
  	cancel() {
  		this.componentDialogVisible = false
  		this.dialogActionType = false
  	},
  	componentDialog (value) {
  		this.selectedNode = value

  		this.componentDialogVisible = true
    	this.componentDialogTitle = value.title
  	},
    // 取消选中
    cancelNodeSelected () {
    	this.CANAEL_SELECTED();
    },
    wheelHandle (ev) {
      if (ev.deltaY < 0) {
        this.zoomType = 'zoomIn'
      } else {
        this.zoomType = 'zoomOut'
      }
      let x = ev.offsetX
      let y = ev.offsetY
      if (this.zoomType === 'zoomIn') {
        if (this.drawStyle.zoomRate < 3) {
          let zoomRate = this.drawStyle.zoomRate + 0.25
          this.UPDATE_DRAWSTYLE({
            zoomRate,
            origin: `${x}px ${y}px`
          })
        }
      } else if (this.zoomType === 'zoomOut') {
        if (this.drawStyle.zoomRate > 0.25) {
          let zoomRate = this.drawStyle.zoomRate - 0.25
          this.UPDATE_DRAWSTYLE({
            zoomRate,
            origin: `${x}px ${y}px`
          })
        }
      }
    },
    updateLine (nodeOffsetX, nodeOffsetY) {
      let { id } = this.hoverNodeData
      let data = {}
      for (var key in this.lineData) {
        const {
          startId,
          endId,
          startPosition: { x: sx, y: sy },
          endPosition: { x: ex, y: ey }
        } = this.lineData[key]
        if (startId === id) {
          data = {
            ...data,
            [key]: {
              ...this.lineData[key],
              startPosition: {
                x: Number(sx) + nodeOffsetX,
                y: Number(sy) + nodeOffsetY
              }
            }
          }
        } else if (endId === id) {
          data = {
            ...data,
            [key]: {
              ...this.lineData[key],
              endPosition: {
                x: Number(ex) + nodeOffsetX,
                y: Number(ey) + nodeOffsetY
              }
            }
          }
        }
      }
      this.UPDATE_LINE(data)
    },
    dropHandle (ev) {
      // 防止拖拽速度过快导致定位错误
      if (ev.target.tagName !== 'DIV') return
      let cont = event.dataTransfer.getData('Text')
      // 为了解决节点移动时位置问题，节点位置为 update:nodeID的形式
      let key = cont.replace(/:.*/g, '')
      // 这边的(40 * this.drawStyle.zoomRate)是设置dragImage时的偏移量，(由于使 iconfont 实际icon与svg存在间隙)
      let x = ev.offsetX - (40 * this.drawStyle.zoomRate)
      let y = ev.offsetY - (40 * this.drawStyle.zoomRate)
      if(x < 0) {
      	x = 0
      }
      if(y < 0) {
      	y = 0
      }
      ev.dataTransfer.dropEffect = 'copy'
      let fn = {
        update () {
          let id = cont.replace(/.*:/g, '')
          // 2018-08-03 修改：此处是节点拖动连线箭头不会跟着节点移动，在放大缩小时，连线位置没有问题
          const { left, top } = this.nodeData[id]
          const nodeOffsetX = x - left
          const nodeOffsetY = y - top
         	this.updateLine(nodeOffsetX, nodeOffsetY);
         	this.UPDATE_NODE({
         	  [id]: {
         	    ...this.nodeData[id],
         	    top: y,
         	    left: x
         	  }
         	})
          // 记录历史状态
		      this.UPDATE_HISTORY_DATA({})
        },
        // 添加节点
        add () {
        	let isInputNode = ev.dataTransfer.getData('isInputNode');	//节点类型是否为输入节点
        	if(this.inputNodeNum > 0 && isInputNode === 'Y') {
        		this.cusMessage("流程中已经存在一个输入节点。", "warning");
        		return
        	}
        	if(isInputNode === 'Y') {
        		this.UPDATE_INPUTNODENUM();
        	}
        	else {
        		this.UPDATE_ISNOTINPUTNODENUM();
        	}
          let id = 'node-' + new Date().getTime();
          let imgSrc = ev.dataTransfer.getData('URL');
          let title = ev.dataTransfer.getData('title');
          this.UPDATE_NODE({
            [id]: {
              id: 'node-' + new Date().getTime(),
              type: this.selNodeType,
              top: y,
              left: x,
              text: '',
              imgSrc,
              title,
              isInputNode
            }
          })
        }
      }
      if (typeof key !== 'undefined') {
        fn[key].call(this)
      }
    }
  },
  mounted () {
    document.addEventListener('contextmenu', ev => {
      ev.preventDefault()
    })
  }
}
</script>

<style lang="scss">
.flow-main-cont {
  right: 0;
  left: 208px;
  top: 110px;
  bottom: 0;
  overflow: auto;
  background: #ebebeb;
  position: absolute;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  .main-bg {
    overflow: hidden;
    width: 5000px;
    height: 5000px;
    background: url('../../assets/bg.png') #fff;
  }
  .draw-wrap {
    flex: 1;
    overflow: auto;
  }
}
.isShowTree {
	margin-left: 208px;
}
</style>
