<template>
  <section class="arrow-wrap"
    :style="arrow"
    v-show="nodeId">
    <img src="~assets/arrow.png" draggable="false" class="arrow"
      v-for="(item,index) in ['c']" :key="index" :class="`arrow-${item}`"
      @mouseover="arrowPointEnter(item)" @mouseleave="arrowPointLeave" @mousedown="drawLineStart(item)"
      @mouseup="drawLineSuccess(item)">
  </section>
</template>

<script>
import { mapState, mapMutations } from 'vuex'
import { deepCopy } from './utils'
import { cloneDeep } from 'lodash/fp'
// import { deepCopy, pxToNumber, getRelativePosition } from './utils'

export default {
  name: 'Arrow',
  data () {
    return {
      pointRectPadding: 0,
      lineDrawing: false,
      lineInfo: {
        lineDrawing: false,
        type: '',
        startPosition: {
          x: 0,
          y: 0
        },
        endPosition: {
          x: 0,
          y: 0
        },
        startId: '',
        endId: ''
      }
    }
  },
  computed: {
    ...mapState('flow', [
      'nodeData',
      'lineData',
      'selLineType',
      'hoverNodeData',
      'hoverArrowPoint',
      'drawStyle'
    ]),
    nodeId () {
      return this.hoverNodeData.id
    },
    arrow () {
      if (!this.nodeId || !this.nodeData[this.nodeId]) {
        return
      }
      let { width, height, left, top, x, y } = this.nodeData[this.nodeId]
      width = `${(Number(width) + this.pointRectPadding).toFixed(0)}px`
      height = `${(Number(height) + this.pointRectPadding).toFixed(0)}px`
      left = Number(left) + Number(x) + 'px'
      top = Number(top) + Number(y) + 'px'
      return {
        width,
        height,
        left,
        top
      }
    }
  },
  methods: {
    ...mapMutations('flow', ['UPDATE_LINE', 'UPDATE_SHADOW_LINE', 'UPDATE_NODE']),
    ...mapMutations('processStore', ['UPDATE_HISTORY_DATA']),
    arrowPointEnter (direction) {},
    arrowPointLeave () {},
    drawLineStart (direction) {
      this.lineDrawing = true
      this.lineInfo = {
        ...this.lineInfo,
        startPosition: { ...this.getPointPosition(event.target) },
        startId: this.nodeId
      }
      this.$parent.$el.addEventListener('mousemove', this.lineDrawingHandle)
    },
    /**
     * 连接拖动时，箭头显示的结束位置
     * 需要除以当前缩放比例，保证箭头是随着鼠标移动
     */
    lineDrawingHandle () {
      const { clientX, clientY } = event
      const { top, left } = this.$parent.$el.getBoundingClientRect()
      const { startPosition } = this.lineInfo
      this.UPDATE_SHADOW_LINE({
        startPosition,
        endPosition: {
          x: (clientX - left)/this.drawStyle.zoomRate,
          y: (clientY - top)/this.drawStyle.zoomRate
        },
        type: this.selLineType
      })
    },
    // 连线成功调用方法
    drawLineSuccess (direction) {
    	// 开始节点
      const { startId } = this.lineInfo;
      // 如果开始节点已经有连线
      var startNode = this.nodeData[startId];
      if(startNode.nextNodeId && startNode.nextNodeId != '') {
      	return
      }
      this.$parent.$el.removeEventListener('mousemove', this.lineDrawingHandle);
      if (!this.lineDrawing || startId === this.nodeId) {
        return
      }
      // 结束节点是输入组件时
      var endNode = this.nodeData[this.nodeId];
      if(endNode.isInputNode === 'Y') {
      	return
      }
      this.lineInfo = {
        ...this.lineInfo,
        endPosition: { ...this.getPointPosition(event.target) },
        endId: this.nodeId
      }
      const lineId = 'line-' + new Date().getTime();
      const lineData = deepCopy(this.lineInfo);
      this.UPDATE_LINE({
        [lineId]: {
          ...lineData,
          lineId,
          type: this.selLineType
        }
      })
      this.lineInfo.lineDrawing = false;
      // 节点信息增加lineId
      var newLineId1 = [];
      if (startNode.lines) {
        newLineId1 = cloneDeep(startNode.lines);
        newLineId1.push(lineId);
      } else {
        newLineId1[0] = lineId;
      }
      this.UPDATE_NODE({
        [startId]: {
          ...startNode,
          lines: newLineId1,
          nextNodeType: this.nodeData[lineData.endId].type,		// 连线下个节点的节点类型
          nextNodeId: this.nodeData[lineData.endId].id
        }
      })
      // 结束节点
      let endNodeId = lineData.endId;
      var newLineId2 = [];
      if (this.nodeData[endNodeId].lines) {
				newLineId2 = cloneDeep(this.nodeData[endNodeId].lines);
        newLineId2.push(lineId);
      } else {
      	newLineId2[0] = lineId;
      }
      this.UPDATE_NODE({
      	[endNodeId]: {
      		lines: newLineId2,
      		...this.nodeData[endNodeId]
      	}
      })
      // 记录历史状态
      this.UPDATE_HISTORY_DATA({});
    },
    getPointPosition (target) {
    	/*let { width, height, left, top, x, y } = this.nodeData[nodeId]
      left = left + x + width/2
      top = top + y + height/2
      console.log(left, top)
      return {
        x: left,
        y: top
      }*/
      const node = this.nodeData[this.nodeId]
      const { top, left, width } = node
      // const {top, left, width, height} = node
      // const { top, left } = getRelativePosition(target, this.$parent.$el);
      // const { width, height } = target.getBoundingClientRect();
      return {
        x: Number(left) + Number(width) / 2,
        y: 40 + Number(top)
      }
    },
    drawLineEnd () {
      this.lineDrawing = false
      this.UPDATE_SHADOW_LINE(null)
      this.$parent.$el.removeEventListener('mousemove', this.lineDrawingHandle)
    }
  },
  mounted () {
    document.addEventListener('mouseup', this.drawLineEnd)
  },
  destroyed () {
    document.removeEventListener('mouseup', this.drawLineEnd)
  }
}
</script>

<style lang="scss">
.arrow-wrap {
  position: absolute;
  cursor: move;
  .arrow {
    opacity: 0.3;
    z-index: 9999;
    cursor: crosshair;
    position: absolute;
    z-index: 99999;
    &-c {
      top: 0;
      left: 50%;
      transform: translate(-50%,105%);
    }
    &-t {
      top: 0;
      left: 50%;
      transform: translate(-50%, -50%);
    }
    &-b {
      bottom: 0;
      left: 50%;
      transform: translate(-50%, 50%);
    }
    &-l {
      bottom: 50%;
      left: 0;
      transform: translate(-50%, 50%);
    }
    &-r {
      bottom: 50%;
      right: 0;
      transform: translate(50%, 50%);
    }
    &:hover {
      opacity: 1;
    }
  }
}
</style>
