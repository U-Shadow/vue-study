import start from '@/components/dialog/StartComponent'
import end from '@/components/dialog/EndComponent'
import jetty from '@/components/dialog/JettyComponent'
import http from '@/components/dialog/HttpComponent'
import jdbc from '@/components/dialog/JdbcComponent'
import dataQuery from '@/components/dialog/DataQueryComponent'
import fileInput from '@/components/dialog/FileInputComponent'
import fileOutput from '@/components/dialog/FileOutputComponent'
import sqlExcute from '@/components/dialog/SqlExcuteComponent'
import JMSOutput from '@/components/dialog/JMSOutputComponent'
import JMSInput from '@/components/dialog/JMSInputComponent'
import JMSFetch from '@/components/dialog/JMSFetchComponent'

export default {
  props: {
    dialogActionType: {
    	type: Boolean,
    	required: true
    },
    nodeData: {
    	type: Object,
    	required: true
    },
    componentDialogTitle: '',
    componentDialogVisible: Boolean
  },
  computed: {
  },
  components: {
  	start,
  	end,
  	jetty,
  	http,
  	jdbc,
  	dataQuery,
  	fileInput,
  	fileOutput,
  	sqlExcute,
  	JMSOutput,
  	JMSInput,
  	JMSFetch
  },
  data () {
  	return {
  	}
  },
  methods: {
    visibleChangeFun(val) {
      this.$emit('visibleChange', val);
    }
  },
  render: function (createElemnt) {
  	const { dialogActionType, nodeData, componentDialogTitle, componentDialogVisible } = this
//		console.log(nodeData)
		const name = nodeData.type
  	return createElemnt(name, {
  		props: {
  			dialogActionType: dialogActionType,
  			_nodeData: nodeData,
        componentDialogTitle: componentDialogTitle,
        componentDialogVisible: componentDialogVisible
  		},
      on: {
        visibleChange: this.visibleChangeFun
      }
  	})
  }
}
