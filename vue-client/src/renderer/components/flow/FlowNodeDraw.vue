<template>
  <div id="FlowNodeDarw"
    class="node-draw"
    ref="FlowNodeDarw"
    @mouseover.capture="drawMouseover">
    <flow-arrow></flow-arrow>
    <flow-node v-for="(item,index) in nodeData" :key="index" :option="item"
      @nodeDragStart="nodeDragStart" @nodeDragging="nodeDragging" @nodeDragEnd="nodeDragEnd"
      @componentDialog="componentDialog">
    </flow-node>
    <!-- <flow-ref-line :refLineData="refLineData"></flow-ref-line> -->
    <!-- 用div模仿连线位置实现连接可选中功能 -->
		<div v-for="(item,index) in lineData" :key="`DIV-${index}`" v-if="lineData"
			:id="index" :class="[selectedLine === index ? linkDivShow : linkDivHidden]" class="flow-node"
			:style="pathDiv(item)" @click.stop.prevent="selectLine(index)">
  	</div>
  </div>
</template>

<script>
import FlowNode from './FlowNode'
//import FlowRefLine from './FlowRefLine'
import FlowArrow from './FlowArrow'

import { mapState, mapMutations } from 'vuex'

export default {
  name: 'FlowNodeDarw',
  data () {
    return {
    	linkDivShow: 'link-div-show',
    	linkDivHidden: 'link-div-hidden',
      dragNodeId: '',
      dragging: false,
      dragNodeInfo: {},
      checkNodesData: [],
      refLineData: [],
      mouseInfo: {
        left: '',
        top: ''
      }
    }
  },
  components: {
    FlowNode,
//  FlowRefLine,
    FlowArrow
  },
  computed: {
    ...mapState('flow', ['nodeData', 'lineData', 'selectedLine'])
  },
  watch: {
    dragNodeId () {
      this.dragNodeInfo = {
        ...this.nodeData[this.dragNodeId]
      }
    }
  },
  methods: {
    ...mapMutations('flow', ['UPDATE_HOVER_NODE', 'SELECTED_LINE']),
    // 确定模仿path的div的长度，位置，以及旋转角度
    pathDiv(lineOption) {
  		let pathPosition = this.position(lineOption);
  		let startPosition = pathPosition.source;
  		let endPosition = pathPosition.target;
			let divLength = this.distance(startPosition, endPosition);
			let triangleHeight = endPosition.y - startPosition.y;
			let triangleLength = endPosition.x - startPosition.x;
			let tansformAngle = this.angleByTan(triangleHeight, triangleLength);
			if(endPosition.x < startPosition.x) {
				tansformAngle = 180 + tansformAngle;
			}
  		return {
				width: (divLength-10) + 'px',
  			transform: 'rotate('+ tansformAngle + 'deg)',	// 旋转角度
				transformOrigin: 'top left',	// 旋转中心
  			top: startPosition.y + 'px',
  			left: startPosition.x + 'px'
  		}
  	},
  	// 获取连线path的开始位置和技术位置
    position (lineOption) {
      let source = {
    		x: Number(lineOption.startPosition.x),
    		y: Number(lineOption.startPosition.y)
    	}
    	let target = {
    		x: Number(lineOption.endPosition.x),
    		y: Number(lineOption.endPosition.y)
    	}
      return {
        source,
        target
      }
    },
    distance (a, b) {
  		// 平方根
      return Math.sqrt((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y));
   	},
   	// 利用正切值计算模仿path连线的div偏转角度
   	angleByTan(a, b) {
   		var atan = Math.atan(a/b)/(Math.PI / 180);
 			return Math.round(atan*100)/100;
   	},
   	selectLine(index) {
   		let lineId = index;
   		this.SELECTED_LINE({lineId});
   	},
    componentDialog (value) {
    	this.$emit('componentDialog', value)
    },
    drawMouseover () {
      if (event.target === event.currentTarget) {
        this.UPDATE_HOVER_NODE({
          id: ''
        })
      }
    },
    // 节点拖动结束调用方法
    nodeDragEnd (payload) {
      this.refLineData = []
    },
    nodeDragging (payload) {
      // let fn = () => {
      //   let { event } = payload;
      //   // 在HTML的结构固定的情况下，可以使用一下方式快捷获取元素定位。
      //   // 否则不建议使用该方法。只可惜drag事件的event无法获取鼠标在当前画布的偏移量。

      //   let { clientX: X, clientY: Y } = event;
      //   let drawContainer = document.getElementById('drawContainer');
      //   let { top: oTop, left: oLeft } = drawContainer.getBoundingClientRect();
      //   let { scrollTop: sTop, scrollLeft: sLeft } = drawContainer;
      //   let left = X - oLeft + sLeft - 20;
      //   let top = Y - oTop + sTop - 23;
      //   let { height, width } = this.nodeData[payload.id];
      //   let right = left + width;
      //   let bottom = top + height;
      //   let dragData = {
      //     left,
      //     top,
      //     right,
      //     bottom,
      //     width,
      //     height,
      //     halfHeight: height / 2,
      //     halfWidth: width / 2
      //   };

      //   this.checkNodesData.forEach(target => {
      //     let {
      //       id,
      //       left,
      //       top,
      //       right,
      //       bottom,
      //       halfWidth,
      //       halfHeight,
      //       width,
      //       height
      //     } = target;
      //     if (id === payload.id) {
      //       return;
      //     }
      //     let conditions = [
      //       // 水平参考线 - 上
      //       {
      //         isNearly: this.isNearly(dragData.top, top),
      //         refLine: ['top', top]
      //       },
      //       {
      //         isNearly: this.isNearly(dragData.top, bottom),
      //         refLine: ['top', bottom]
      //       },
      //       // 水平参考线 - 中
      //       {
      //         isNearly: this.isNearly(
      //           dragData.top + dragData.halfHeight,
      //           top + halfHeight
      //         ),
      //         refLine: ['top', top + halfHeight]
      //       },
      //       // 水平参考线 - 下
      //       {
      //         isNearly: this.isNearly(dragData.bottom, top),
      //         refLine: ['top', top]
      //       },
      //       {
      //         isNearly: this.isNearly(dragData.bottom, bottom),
      //         refLine: ['top', bottom]
      //       },
      //       // 垂直参考线 - 左
      //       {
      //         isNearly: this.isNearly(dragData.left, left),
      //         refLine: ['left', left]
      //       },
      //       {
      //         isNearly: this.isNearly(dragData.left, right),
      //         refLine: ['left', right]
      //       },
      //       // 垂直参考线 - 右
      //       {
      //         isNearly: this.isNearly(dragData.right, left),
      //         refLine: ['left', left]
      //       },
      //       {
      //         isNearly: this.isNearly(dragData.right, right),
      //         refLine: ['left', right]
      //       },
      //       // 垂直参考线 - 中
      //       {
      //         isNearly: this.isNearly(
      //           dragData.left + dragData.halfWidth,
      //           left + halfWidth
      //         ),
      //         refLine: ['left', left + halfWidth]
      //       }
      //     ];
      //     this.refLineData = conditions;
      //   });
      // };
      // // 函数节流
      // fn();
    },
    isNearly (dragValue, targetValue) {
      return Math.abs(dragValue - targetValue) <= 3
    },
    nodeDragStart (id) {
      this.checkNodes(id)
    },
    checkNodes (id) {
//    console.log(this.nodeData);
      let checkNodes = Object.values(this.nodeData)

      this.checkNodesData = checkNodes.map(element => {
//      let { width, height, left, top, x, y, id } = element
        let width = Number(element.width)
        let height = Number(element.height)
        let left = Number(element.left)
        let top = Number(element.top)
        let x = Number(element.x)
        let y = Number(element.y)
        let id = element.id
        let right, bottom, halfWidth, halfHeight
        left += x
        top += y
        right = left + width
        bottom = top + height
        halfWidth = width / 2
        halfHeight = height / 2
        return {
          id,
          left,
          top,
          right,
          bottom,
          halfWidth,
          halfHeight,
          width,
          height
        }
      })
    }
  },
  mounted () {}
}
</script>

<style scoped>
.node-draw {
  position: relative;
  width: 100%;
  height: 100%;
  z-index: 9999;
}
.flow-node {
  position: absolute;
  z-index: 9999;
  &.flow-node:hover a {
    border-right: 1px solid #000;
  }
}
.link-div-show {
	/*border: 1px solid rgba(51, 51, 51, 0.25);*/
	height: 2px;
	background-color: rgba(51, 51, 51, 0.25);
	box-shadow:0px -1px 10px #333333;
	margin-top: -1px;
}
.link-div-hidden {
	/*border: 1px solid rgba(51, 51, 51, 0.25);*/
	height: 2px;
	background-color: rgba(51, 51, 51, 0.25);
	margin-top: -1px;
}
</style>
