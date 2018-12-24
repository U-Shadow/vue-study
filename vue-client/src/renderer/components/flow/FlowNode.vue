<template>
  <div :tabindex="tabindexFormat(option.id)" class="flow-node" :draggable="nodeInfo.draggable" :style="nodePosition"
  	@dragstart="nodeDragStart(option.id)" @drag="nodeDragging(option.id)" @dragend="nodeDragEnd(option.id)"
  	@mouseenter="mouseEnterNode(option.id)" @click.stop="nodeSelectedFun(option.id)" @dblclick="componentDialog(option)">
    <icon :name="option.type" :size="80" :ref="option.id" style="cursor:move" />
  </div>
</template>

<script>
import { mapState, mapMutations } from 'vuex'

export default {
  name: 'node',
  data () {
    return {
      arrowVisible: false,
      nodeInfo: {
        draggable: true
      }
    }
  },
  props: {
    option: {
      type: Object
    }
  },
  computed: {
    ...mapState('flow', ['nodeData', 'lineData', 'drawStyle']),
    nodePosition () {
      return {
        left: `${this.option.left}px`,
        top: `${this.option.top}px`
      }
    }
  },
  methods: {
    ...mapMutations('flow', [
      'UPDATE_NODE',
      'UPDATE_HOVER_NODE',
      'UPDATE_LINE',
      'SELECTED_NODE'
    ]),
    ...mapMutations('processStore', ['UPDATE_HISTORY_DATA']),
    componentDialog (node) {
    	this.$emit('componentDialog', node)
    },
    nodeDragStart (id) {
      let img = this.$el
      let dataTransfer = event.dataTransfer
      dataTransfer.dropEffect = 'move'
      dataTransfer.setData('Text', `update:${this.option.id}`)
      dataTransfer.setDragImage(img, 40, 40)
      this.$emit('nodeDragStart', id)
      var nodeId = id;
      this.SELECTED_NODE({
        nodeId
      })
    },
    /**
     * 节点拖动事件
     * 2018-08-03 修改：此处是节点拖动另一种方式，连线箭头跟着节点移动，但是在放大缩小时，连线位置有问题
     */
    nodeDragging (id) {
      // this.$emit('nodeDragging', { id, event });
//    const x = event.clientX
//    const y = event.clientY
//    const { height } = this.nodeData[id]
//    const nodeOffsetX = x - 208
//    const nodeOffsetY = y - 68 - height
//    this.updateLine(Number(nodeOffsetX), Number(nodeOffsetY), id)
    },
    updateLine (nodeOffsetX, nodeOffsetY, id) {
      let data = {}
      for (var key in this.lineData) {
        const {startId, endId} = this.lineData[key]

        if (startId === id) {
          data = {
            ...data,
            [key]: {
              ...this.lineData[key],
              startPosition: {
                x: nodeOffsetX,
                y: nodeOffsetY
              }
            }
          }
        } else if (endId === id) {
          data = {
            ...data,
            [key]: {
              ...this.lineData[key],
              endPosition: {
                x: nodeOffsetX,
                y: nodeOffsetY
              }
            }
          }
        }
      }
      this.UPDATE_LINE(data)
    },
    nodeDragEnd (id) {
      this.$emit('nodeDragEnd', { id, event })
    },
    mouseEnterNode (id) {
      this.UPDATE_HOVER_NODE({
        id
      })
    },
    getNodeSize () {
      let id = this.option.id
      let { width, height, x, y } = this.$refs[id].$el.childNodes[0].getBBox()
      width = +width.toFixed(0)
      height = +(height - 3).toFixed(0)
      x = +x.toFixed(0)
      y = +y.toFixed(0)

      this.UPDATE_NODE({
        [id]: {
          ...this.nodeData[id],
          width,
          height,
          x,
          y
        }
      })

      // 记录历史状态
      this.UPDATE_HISTORY_DATA({})
    },
    init () {
      let id = this.option.id
      if (typeof this.nodeData[id].width === 'undefined') {
        this.getNodeSize()
      }
    },
    // 选中节点
    nodeSelectedFun (nodeId) {
 	    // console.log(this.nodeData[nodeId])
     	this.SELECTED_NODE({
        nodeId
      })
    },
    // tabindex格式化,div加tabindex可以用:focus伪类
    tabindexFormat (tabindex) {
      var id = tabindex.substr(tabindex.length - 8, tabindex.length)
      return id
    }
  },
  mounted () {
    this.init()
  }
}
</script>

<style lang="scss">
.flow-node {
  position: absolute;
  z-index: 9999;
  &.flow-node:hover a {
    border-right: 1px solid #000;
  }
}
</style>
