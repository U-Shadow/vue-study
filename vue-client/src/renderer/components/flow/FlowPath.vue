<script>
// import { mapState, mapMutations } from 'vuex'
import * as d3 from 'd3'

export default {
  name: 'FlowPath',
  render (h) {
    const a = (
      <path id="link-1-2" class="link" marker-end="url(#markerArrow)" d={this.optionD} />
    )
    return a
  },
  data () {
    return {
      dd: ''
    }
  },
  props: {
    lineOption: {
      type: Object
    }
  },
  computed: {
    option () {
      let source = {
    		x: Number(this.lineOption.startPosition.x),
    		y: Number(this.lineOption.startPosition.y)
    	}
    	let target = {
    		x: Number(this.lineOption.endPosition.x),
    		y: Number(this.lineOption.endPosition.y)
    	}
      return {
        source,
        target
      }
    },
    nodeData () {
      return [
        {
          id: '1',
          x: 200,
          y: 200,
          name: 'p1'
        },
        {
          id: '2',
          x: 200,
          y: 300,
          name: 'p2'
        },
        {
          id: '3',
          x: 500,
          y: 200,
          name: 'p3'
        },
        {
          id: '4',
          x: 600,
          y: 290,
          name: 'p4'
        },
        {
          id: '5',
          x: 500,
          y: 390,
          name: 'p5'
        },
        {
          id: '6',
          x: 500,
          y: 450,
          name: 'p6'
        }
      ]
    },
    link () {
      const { sc, tc } = this.controlPointLayout()
      return {
        source: this.option.source,
        target: this.option.target,
        sc,
        tc
      }
    },
    /**
     * path连线渲染, 注释点中间两个后为直线，否则为曲线
     */
    optionD () {
      const points = [
        {
          x: this.link.source.x,
          y: this.link.source.y
        },
//      {
//        x: this.link.sc.x,
//        y: this.link.sc.y
//      },
//      {
//        x: this.link.tc.x,
//        y: this.link.tc.y
//      },
        {
          x: this.link.target.x,
          y: this.link.target.y
        }
      ]
//    this.controlPointLayout(this.link, this.link)
      const line = d3
        .line()
        .x(d => d.x)
        .y(d => d.y)
        .curve(d3.curveBasis)
      return line(points)
    }
  },
  methods: {
    distance (a, b) {
      return Math.sqrt((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y))
    },
    mid3 (a, b) {
      return {
        mass: 1,
        vx: 0,
        vy: 0,
        fx: 0,
        fy: 0,
        x: (a.x * 2 + b.x) / 3,
        y: (a.y * 2 + b.y) / 3
      }
    },
    controlPointLayout () {
      const gravity = -10
      const springLength = 0
      const springCoeff = 0.0008
      const timeStep = 5
      const dragCoeff = 0.02
      const nodeMass = 3
      const times = 100
      let sc = this.mid3(this.option.source, this.option.target)
      let tc = this.mid3(this.option.target, this.option.source)
      let r
      let force = gravity * sc.mass * nodeMass / (r * r * r)

      for (let i = 0; i < times; ++i) {
        sc.fx = 0
        tc.fx = 0
        sc.fy = 0
        tc.fy = 0
        this.nodeData.forEach(node => {
          // 计算节点给控制节点的排斥力
          r = this.distance(node, sc)
          force = gravity * sc.mass * nodeMass / (r * r * r)
          sc.fx += (node.x - sc.x) * force
          sc.fy += (node.y - sc.y) * force

          r = this.distance(node, tc)
          force = gravity * tc.mass * nodeMass / (r * r * r)
          tc.fx += (node.x - tc.x) * force
          tc.fy += (node.y - tc.y) * force
        })
        r = this.distance(sc, tc)
        force = gravity * sc.mass * tc.mass / (r * r * r)
        sc.fx += (tc.x - sc.x) * force
        sc.fy += (tc.y - sc.y) * force
        tc.fx += (sc.x - tc.x) * force
        tc.fy += (sc.y - tc.y) * force

        r = this.distance(this.option.source, sc)
        sc.fx +=
          (this.option.source.x - sc.x) * springCoeff * (r - springLength) / r
        sc.fy +=
          (this.option.source.y - sc.y) * springCoeff * (r - springLength) / r

        r = this.distance(sc, tc)
        sc.fx += (tc.x - sc.x) * springCoeff * (r - springLength) / r
        sc.fy += (tc.y - sc.y) * springCoeff * (r - springLength) / r
        tc.fx -= (tc.x - sc.x) * springCoeff * (r - springLength) / r
        tc.fy -= (tc.y - sc.y) * springCoeff * (r - springLength) / r

        r = this.distance(this.option.target, tc)
        tc.fx +=
          (this.option.target.x - tc.x) * springCoeff * (r - springLength) / r
        tc.fy +=
          (this.option.target.y - tc.y) * springCoeff * (r - springLength) / r

        sc.fx -= sc.vx * dragCoeff
        sc.fy -= sc.vy * dragCoeff
        tc.fx -= tc.vx * dragCoeff
        tc.fy -= tc.vy * dragCoeff

        sc.vx += sc.fx * timeStep
        sc.vy += sc.fy * timeStep
        sc.x += sc.vx * timeStep
        sc.y += sc.vy * timeStep

        tc.vx += tc.fx * timeStep
        tc.vy += tc.fy * timeStep
        tc.x += tc.vx * timeStep
        tc.y += tc.vy * timeStep
        // sc.x = 112;
        // sc.y = 218;
        // tc.x = 112;
        // tc.y = 280;
      }
      return {
        sc,
        tc
      }
    }
  },
  mounted () {

  }
}
</script>

<style lang="scss">
.link {
  stroke: #000;
  stroke-width: 1.5px;
  fill: none;
}
</style>
