<script>
import { mapMutations, mapState } from 'vuex'
import ToolMenu from './ToolMenu'
import fs from 'fs'
import path from 'path'
import os from 'os'
export default {
  name: 'flow-top-tool',
  render (h) {
    const topTool = this.topTool
    const labelCont = (item) => {
      if (item.type === 'label') {
        return (<span>{this.zoomRateNum}</span>)
      }
    }
    const iconCont = (item) => {
      let color
      if (typeof item.color === 'function') {
        color = item.color.call(this)
      } else {
        color = item.color
      }
      return (<icon name={item.icon} size={item.size} style={{color}} />)
    }
    const items = topTool.map((item, index) => {
      return (
        <div class='ftt-tool-item' data-event={item.event} title={item.title}>
          <div class={['ftt-icon', (item.type === 'label' ? 'ftt-label' : 'ftt-button')]}>
            {labelCont(item)}
            {iconCont(item)}
          </div>
          <div class='ftt-separator' v-show={item.separator} />
        </div>
      )
    })
    return (
      <div id='flowTopTool' class='flow-top-tool' >
        <div class='ftt-container' onClick={this.toolDelegate}>
          {items}
        </div>
        <tool-menu ulStyle={'width:100px;text-align:center'} visible={this.visible} menuData={this.menuData} onSelItme={this.menuClickFn} />
      </div>
    )
  },
  data () {
    return {
    	// 数据库组件beans
    	databaseBeans: '',
    	// 数据源beans
    	dataSourcesBeans: '',
    	// jms activeMq beans
    	jmsBeans: '',
    	fileName: '',
      visible: false,
      topTool: [
        {
          type: 'label',
          title: '画布缩放',
          icon: 'down-arrow',
          size: 10,
          separator: true,
          event: 'selZoomRate'
        },
        {
          type: 'text',
          title: '直线',
          icon: 'straight',
          separator: true,
//        event: 'straight',
          color () {
            return (this.selLineType === 'StraightLine' ? '#f00' : '')
          }
        },
        // {
        //   type: 'button',
        //   title: '折线',
        //   icon: 'zhexianjiantou1',
        //   separator: true,
        //   event: 'poly',
        //   color () {
        //     return (this.selLineType === 'LinePoly' ? '#f00' : '')
        //   }
        // },
        {
          type: 'button',
          title: '放大',
          icon: 'expand',
          event: 'zoomIn'
        },
        {
          type: 'button',
          title: '缩小',
          icon: 'narrow',
          separator: true,
          event: 'zoomOut'
        },
        {
          type: 'button',
          title: '保存',
          icon: 'save',
//        separator: true,
          event: 'save',
          color() {
          	return (this.processStatus === 'edit' ? '' : '#999')
          }
        },
        {
          type: 'button',
          title: '启动',
          icon: 'run',
          event: 'run',
          color () {
            return (this.processStatus === 'save' ? '' : '#999')
          }
        },
        {
          type: 'button',
          title: '停止',
          icon: 'stop',
          separator: true,
          event: 'stop',
          color () {
            return (this.processStatus === 'run' ? '' : '#999')
          }
        },
        {
          type: 'button',
          title: '删除',
          icon: 'remove',
          separator: true,
          event: 'remove',
          color () {
            return (this.showRemove ? '' : '#999')
          }
        },
        {
          type: 'button',
          title: '撤销',
          icon: 'undo',
          event: 'undo',
          color () {
            return (this.showUndo ? '' : '#999')
          }
        },
        {
          type: 'button',
          title: '重做',
          icon: 'redo',
          separator: true,
          event: 'redo',
          color () {
            return (this.showRedo ? '' : '#999')
          }
        }
      ],
      menuData: [
        // {
        //   title: '25%'
        // },
        {
          title: '50%'
        },
        {
          title: '75%'
        },
        {
          title: '100%'
        },
        {
          title: '125%'
        },
        {
          title: '150%'
        },
        {
          title: '200%'
        },
        {
          title: '300%'
        }
      ]
    }
  },
  components: {
    ToolMenu
  },
  computed: {
    ...mapState('flow', ['nodeData', 'selLineType', 'drawStyle', 'historyLength', 'historyIndex', 'lineData',
    	'selectedNode', 'componentData', 'processInfo', 'processStatus',
    	'currentProcess', 'isNotInputNodeNum', 'inputNodeNum', 'selectedLine'
    ]),
    ...mapState('processStore', ['processArray', 'processTree', 'datasourceTree', 'p_historys', 'p_historys_update']),
    zoomRateNum () {
      // console.log("=======================================");
      // console.log( this.drawStyle);
      return this.drawStyle.zoomRate * 100 + '%'
    },
    // 撤销按钮可用
    showUndo () {
    	if(this.p_historys_update) {
    		// 历史数据是否有
	    	var p_history = this.p_historys[this.currentProcess];
	    	if(p_history) {
	    		// 当前版本
	    		var p_currentIndex = p_history.currentIndex;
	    		if(p_currentIndex >0) {
			    	var p_isUnDo = p_history.isUnDo;
			  		return p_isUnDo;
	    		}
	    	}
    	}
    	return false;
    },
    // 重做按钮可用
    showRedo () {
    	if(this.p_historys_update) {
	    	var p_history = this.p_historys[this.currentProcess];
	    	if(p_history) {
		    	var p_historyIndex = p_history.historyIndex;
//		    	var p_historyData = p_history.historyData;
		    	// 当前版本
	    		var p_currentIndex = p_history.currentIndex;
		    	if(p_currentIndex < p_historyIndex-1){
		    		return true;
		    	}
	    	}
    	}
    	return false;
    },
    showRemove () {
    	return this.selectedNode !== '' || this.selectedLine !== ''
    },
    // camel配置文件头部串
    xmlHeaders() {
    	let xmlHeaders = '<beans xmlns="http://www.springframework.org/schema/beans" '
				+ '	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:cxf="http://camel.apache.org/schema/cxf" '
				+ '	xmlns:jaxrs="http://cxf.apache.org/jaxrs" ' + os.EOL
				+ '	xsi:schemaLocation="'
			  + '	http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd' + os.EOL
			  + '	http://camel.apache.org/schema/cxf http://camel.apache.org/schema/cxf/camel-cxf.xsd' + os.EOL
			  + '	http://cxf.apache.org/jaxrs http://cxf.apache.org/schemas/jaxrs.xsd' + os.EOL
			  + '	http://camel.apache.org/schema/spring http://camel.apache.org/schema/spring/camel-spring.xsd">' + os.EOL
			return xmlHeaders
    },
    // camel配置数据格式化串
    dataFormats() {
    	let dataFormats = '<dataFormats>' + os.EOL
				+	'<json id="xstream" prettyPrint="true" />' + os.EOL
				+ '<json id="jackson" prettyPrint="true" library="Jackson" />' + os.EOL
				+ '<json id="gson" prettyPrint="true" library="Gson" />' + os.EOL
				+ '<xmljson id="xmljson" />' + os.EOL
				+ '<xmljson id="xmljsonWithOptions" forceTopLevelObject="true" trimSpaces="true" rootName="newRoot" skipNamespaces="true"' + os.EOL
				+ 'removeNamespacePrefixes="true" expandableProperties="d e" />' + os.EOL
				+ '</dataFormats>' + os.EOL
			return dataFormats
    },
    // camle配置文件中添加相关beams
    camelBeans() {
    	let camelBeans = '<bean id="reviveProcessor" class="com.example.camel.test.camel.processor.ReviveProcessor">' + os.EOL
				+ '</bean>' + os.EOL
				+ '<bean id="fileBeanServer" class="com.example.camel.test.camel.beans.FileBeanServer">' + os.EOL
				+ '</bean>'
				+ this.jmsBeans
				+ this.databaseBeans
				+ this.dataSourcesBeans + os.EOL
			return camelBeans
    },
    // 保存文件输出文件目录路径
    outputDirPath() {
    	let processSpacePath = this.$workspacePath + 'liucheng_' + this.processInfo[this.currentProcess].processName + '/';
    	return processSpacePath;
    }
  },
  watch: {
    visible (val) {
      if (val) {
        document.addEventListener('click', this.documentClick)
      } else {
        document.removeEventListener('click', this.documentClick)
      }
    }
  },
  methods: {
    ...mapMutations('flow', ['SEL_LINETYPE', 'UPDATE_DRAWSTYLE', 'REMOVE_NODE', 'UPDATE_PROCESS_STATUS']),
    ...mapMutations('processStore', ['UNDO', 'REDO']),
    toolDelegate (event) {
      event.stopPropagation()
      let dataset = event.target.dataset
      let eventName = dataset.event
      let eventFn = {
        selZoomRate () {
          this.visible = true
        },
        /*straight () {
          this.SEL_LINETYPE('StraightLine')
        },
        poly () {
          this.SEL_LINETYPE('LinePoly')
        },*/
        zoomIn () {
        	let oldZoomRate = this.drawStyle.zoomRate
          if (oldZoomRate < 3) {
            let zoomRate = this.drawStyle.zoomRate + 0.25
            this.UPDATE_DRAWSTYLE({ zoomRate, oldZoomRate })
          }
        },
        zoomOut () {
        	let oldZoomRate = this.drawStyle.zoomRate
          if (oldZoomRate > 0.5) {
            let zoomRate = this.drawStyle.zoomRate - 0.25
            this.UPDATE_DRAWSTYLE({ zoomRate, oldZoomRate })
          }
        },
        undo () {
          if (this.showUndo) {
            this.UNDO({})
          }
        },
        redo () {
          if (this.showRedo) {
            this.REDO({})
          }
        },
        // 删除选中节点,及其相关连线
        remove () {
        	this.REMOVE_NODE({})
        },
        save () {
        	var directory = this.currentProcess
        	if(JSON.stringify(this.nodeData) === '{}') return
        	this.UPDATE_PROCESS_STATUS('save')
	        this.saveFile(directory)
	        this.cusMessage('保存成功', 'success')
        },
        run(){
        	if(this.processStatus === 'save') {
        		if(this.inputNodeNum == 0){
        			this.cusMessage("流程必须有一个输入节点。", "warning");
        			return
        		}
        		if(this.isNotInputNodeNum == 0){
        			this.cusMessage("流程至少有一个非输入节点。", "warning");
        			return
        		}
        		this.$http.post('/api/runProcess', {
        			path: this.outputDirPath + 'camel_' + this.currentProcess + '.xml',
        			porcessName: this.currentProcess
        		})
        		.then(res => {
        			var result = res.data
        			if(result.code == 200) {
        				this.UPDATE_PROCESS_STATUS('run')
        				this.cusMessage('启动成功', 'success')
        			}
        			else {
        				this.cusMessage('启动失败', 'error')
        			}
        		})
        		.catch(err => {
        			console.log(err)
        			this.cusMessage(err.response.statusText, 'error')
        		})
        	}
        },
        stop(){
        	if(this.processStatus === 'run') {
        		this.$http.post('/api/stopProcess', {
        			path: this.outputDirPath + 'camel_' + this.currentProcess + '.xml',
        			porcessName: this.currentProcess
        		})
        		.then(res => {
        			var result = res.data
        			if(result.code == 200) {
        				this.UPDATE_PROCESS_STATUS('save')
        				this.cusMessage('关闭流程成功', 'success')
        			}
        			else {
        				this.cusMessage('关闭流程失败', 'error')
        			}
        		})
        		.catch(err => {
        			console.log(err)
        			this.cusMessage(err.response.statusText, 'error')
        		})
        	}
        }
      }
      if (this.visible) {
        this.visible = false
      }
      if (eventName) {
        eventFn[eventName].call(this)
      }
    },
    saveFile(directory) {
//  	let processSpacePath = this.$workspacePath + '/liucheng_' + this.processInfo[this.currentProcess].processName + '/';
    	let flowData = this.processArray[this.currentProcess];
    	let data = this.$x2js.js2xml(flowData);
    	// 工作空间根目录
    	if(!fs.existsSync(this.$workspacePath)) {
    		fs.mkdirSync(this.$workspacePath)
    	}
    	fs.writeFile(this.outputDirPath + this.currentProcess + '.xml',
    		'<config>' + data + '</config>', function(err) {
	    		if(err) {
	    			console.error(err);
	    			return console.error("创建流程数据文件失败");
	    		}
    	});
    	// 创建camel xml文件
    	var camelData = this.makeCamelData(this.outputDirPath);
			var camelXml = this.xmlString(this.outputDirPath, camelData)
			var camelPath = path.join(this.outputDirPath, 'camel_' + this.currentProcess + '.xml')	// 流程配置文件路径
			fs.writeFile(camelPath, camelXml, function(err) {
		   	if (err) {
	       	return console.error(err);
		   	}
//		   	console.log('camel xml文件创建成功！');
			})
    	/*
    	// 创建目录
    	let root = process.cwd() + '/output/'	// 目录根路径
    	if(!fs.existsSync(root)) {
    		fs.mkdirSync(root)
    	}
			var dirPath = root + directory	// 文件输出路径
			this.outputDirPath = dirPath
			if(!fs.existsSync(dirPath)) {
				fs.mkdirSync(dirPath)
			}
			// 创建process xml文件
			var processXml = this.processString(dirPath)
			var processPath = path.join(dirPath, '/' + this.fileName)	// 流程配置文件路径
			fs.writeFile(processPath, processXml.process, function(err) {
		   	if (err) {
	       	return console.error(err);
		   	}
//		   	console.log('process xml文件创建成功！');
			})
			// 创建camel xml文件
			var camelXml = this.xmlString(dirPath, processXml.camelData)
			var camelPath = path.join(dirPath, '/camel_' + this.fileName)	// 流程配置文件路径
			fs.writeFile(camelPath, camelXml, function(err) {
		   	if (err) {
	       	return console.error(err);
		   	}
//		   	console.log('camel xml文件创建成功！');
			})
			*/
    },
    /**
     * camel内容字符串
     * @param {String} dirPath 工作空间路径
     */
    makeCamelData(dirPath) {
    	var camelData = '';
    	var i = 0
    	var that =this;
    	let componentData = this.componentData;
    	if(componentData) {
				Object.keys(componentData).forEach(function(key) {
					var component = componentData[key];
					// camel配置内容
					camelData += that.camelFile(++i, dirPath, component, that.nodeData[key]);
				})
			}
    	return camelData
    },
    /**
		 * 流程节点关系字符串
		 * 返回流程关系文件以及生成camel文件的route
		 */
		processString(dirPath) {
			var camelData = ''
	  	var process = '<process>' + os.EOL
			// 数据源
	  	process += this.objForEach(this.datasourceTree, 'dataSource')
	  	// 节点数据
	  	process += this.objForEach(this.nodeData, 'node')
	  	// 连线数据
	  	process += this.objForEach(this.lineData, 'line')
			// 组件数据
			let componentXml = ''
			let componentData = this.componentData
			var i = 0
			var that = this
			if(componentData) {
				Object.keys(componentData).forEach(function(key) {
					var component = componentData[key]
					componentXml += '<componentData id="' + key + '">' + os.EOL
					componentXml += '' + JSON.stringify(component) + os.EOL
					componentXml += '</componentData>' + os.EOL
					// camel配置内容
					camelData += that.camelFile(++i, dirPath, component, that.nodeData[key])
				})
			}
			process = process + componentXml + '</process>' + os.EOL
			return {process, camelData}
	  },
	  objForEach(objs, lableName) {
	  	var str = ''
	  	if(objs) {
				Object.keys(objs).forEach(function(key) {
					var obj = objs[key]
					str += '<' + lableName + ' id="' + key + '" type="' + obj.type + '">' + os.EOL
					str += '' + JSON.stringify(obj) + os.EOL
					str += '</' + lableName + '>' + os.EOL
				})
			}
	  	return str
	  },
	  /**
	   * 返回生成camel文件的route字符串
	   */
	  camelFile(index, dirPath, component, nodeData) {
	  	var camelData = os.EOL + '<route id="' + nodeData.id + '">' + os.EOL
	  	var nodeType = component.nodeType
	  	switch(nodeType) {
	  		case 'jetty':
	  			/*
	  			var name = this.fileName.substring(0, this.fileName.indexOf('.'))
	  			var path = dirPath + '/' + name + '_' + nodeType + index + '.groovy'
	  			// 创建groovy文件
					fs.writeFile(path, component.code, function(err) {
						if(err) {
							console.log(err)
						}
//						console.log('jetty groovy文件创建成功！');
					})*/
					// camel xml文件需要的数据
					camelData += '<from uri="jetty://' + component.data.address + '" />' + os.EOL
//					camelData += '<filter>' + os.EOL
//					camelData += '<groovy>resource:file:' + path + '</groovy>' + os.EOL
//					camelData += '<marshal ref="xmljson" />' + os.EOL
//					camelData += '</filter>' + os.EOL
	  			break
	  		case 'http':
					// camel xml文件需要的数据
					let data = component.data
					camelData += '<from uri="direct:' + nodeData.id + '" />' + os.EOL
					camelData += '<setHeader headerName="CamelHttpMethod">' + os.EOL
			  	camelData += '<constant>' + data.method + '</constant>' + os.EOL
					camelData += '</setHeader>' + os.EOL
					/*
					let paramsData = JSON.stringify(data.paramsData)
					// 这里是camel设置CamelHttpQuery, 接收需要使用Exchange
					camelData += '<setHeader headerName="CamelHttpQuery">' + os.EOL
			  	camelData += '<constant>' + paramsData.substring(1, paramsData.length-1) + '</constant>' + os.EOL
					camelData += '</setHeader>' + os.EOL
					camelData += '<to uri="' + data.address + '" />' + os.EOL
					*/
					camelData += '<to uri="' + data.address + '?' + data.requestParams + '" />' + os.EOL
	  			break
	  		case 'fileInput':
					camelData += '<from uri="direct:' + nodeData.id + '" />' + os.EOL
					// 要读取的文件路径
					camelData += '<setHeader headerName="filePath"><constant>' + os.EOL
						+ component.data.filePath + os.EOL
						+ '</constant></setHeader>' + os.EOL
					// 保持读取文件结果的变量名称
					camelData += '<setHeader headerName="resVariable"><constant>' + os.EOL
						+ component.data.resVariable + os.EOL
						+ '</constant></setHeader>' + os.EOL
					camelData += '<bean ref="bean:fileBeanServer" method="fileInput"/>' + os.EOL
					break
				case 'fileOutput':
					let bodyData = component.data.output
					let tmpPath = (component.data.filePath).replace(/\\/g, '/')
					let tmpInd = tmpPath.lastIndexOf('/')
					let tmpFileName = tmpPath.substring(tmpInd + 1, tmpPath.length)
					let tmpDirPath = tmpPath.substring(0, tmpInd)
					camelData += '<from uri="direct:' + nodeData.id + '" />' + os.EOL
					camelData += '<setBody><simple>' + os.EOL
	  				+ bodyData.replace(new RegExp('\\{\\(', 'gm'), '${in.body.get(') + os.EOL
	  				+ '</simple></setBody>' + os.EOL
	  				+ '<to uri="file:' + tmpDirPath + '?fileName=' + tmpFileName
	  				+ '&amp;charset=' + component.data.encoding
	  				+ '&amp;fileExist=' + component.data.outputWay +'"/>' + os.EOL
					break
				case 'dataQuery':
	  			this.addDatasourceBeans();
	  			this.addDatabaseBeans();
					// camel xml文件需要的数据
					camelData += '<from uri="direct:' + nodeData.id + '" />' + os.EOL
					camelData += '<setHeader headerName="resVariable"><constant>' + os.EOL
						+ component.data.result + os.EOL
						+ '</constant></setHeader>' + os.EOL
					camelData += '<setBody>' + os.EOL
			  	camelData += '<constant>'+ os.EOL + component.data.sql + os.EOL
			  	camelData += '</constant>' + os.EOL
					camelData += '</setBody>' + os.EOL
					camelData += '<to uri="jdbc:datasource-' + component.data.datasource.sourceName + '"/>' + os.EOL
					camelData += '<convertBodyTo type="String"/>' + os.EOL
					camelData += '<bean ref="bean:databaseBeanServer" method="dataQuery"/>' + os.EOL
	  			break
				case 'sqlExcute':
					this.addDatasourceBeans();
					camelData += '<from uri="direct:' + nodeData.id + '" />' + os.EOL
					camelData += '<setBody>' + os.EOL
			  	camelData += '<constant>'+ os.EOL + component.data.sql + os.EOL
			  	camelData += '</constant>' + os.EOL
					camelData += '</setBody>' + os.EOL
					camelData += '<to uri="jdbc:datasource-' + component.data.datasource.sourceName + '"/>' + os.EOL
					camelData += '<convertBodyTo type="String"/>' + os.EOL
	  			break
	  		case 'JMSOutput':
	  			this.addJMSBeans(component);
					camelData += '<from uri="direct:' + nodeData.id + '" />' + os.EOL
					camelData += '<setBody>' + os.EOL
			  	camelData += '<constant>'+ os.EOL + component.data.output + os.EOL
			  	camelData += '</constant>' + os.EOL
					camelData += '</setBody>' + os.EOL
					camelData += '<to uri="jms:queue:'
						+ component.data.queueName
						+ '?jmsMessageType=Text&amp;disableReplyTo=true"/>' + os.EOL
	  			break
	  		case 'JMSInput':
	  			this.addJMSBeans(component);
					camelData += '<from uri="jms:queue:' + component.data.queueName + '" />' + os.EOL
	  			break
	  		case 'JMSFetch':
	  			this.addJMSBeans(component);
					camelData += '<from uri="direct:' + nodeData.id + '" />' + os.EOL
					camelData += '<setHeader headerName="anonymousConn"><constant>' + os.EOL
						+ component.anonymousConn + os.EOL
						+ '</constant></setHeader>' + os.EOL
					if(!component.anonymousConn) {
						camelData += '<setHeader headerName="userName"><constant>' + os.EOL
							+ component.data.userName + os.EOL
							+ '</constant></setHeader>' + os.EOL
						camelData += '<setHeader headerName="password"><constant>' + os.EOL
							+ component.data.password + os.EOL
							+ '</constant></setHeader>' + os.EOL
					}
					camelData += '<setHeader headerName="brokerURL"><constant>' + os.EOL
							+ component.data.jndiAddress + os.EOL
							+ '</constant></setHeader>' + os.EOL
					camelData += '<setHeader headerName="queueName"><constant>' + os.EOL
							+ component.data.queueName + os.EOL
							+ '</constant></setHeader>' + os.EOL
					camelData += '<setHeader headerName="filter"><constant>' + os.EOL
							+ component.data.filter + os.EOL
							+ '</constant></setHeader>' + os.EOL
					camelData += '<setHeader headerName="timeout"><constant>' + os.EOL
							+ component.data.timeout + os.EOL
							+ '</constant></setHeader>' + os.EOL
					camelData += '<setHeader headerName="msgDataName"><constant>' + os.EOL
							+ component.data.msgDataName + os.EOL
							+ '</constant></setHeader>' + os.EOL
					camelData += '<bean ref="bean:mqBeanServer" method="mqFetch"/>' + os.EOL
	  			break
	  		default:
	  			break
	  	}
	  	var nextNodeType = nodeData.nextNodeType
	  	if(nextNodeType) {
	  		if(nextNodeType === 'http') {
	  			camelData += '<removeHeaders pattern="CamelHttp*"/>' + os.EOL
	  		}
	  		camelData += '<to uri="direct:' + nodeData.nextNodeId + '"/>' + os.EOL
			}
	  	camelData += '</route>' + os.EOL
	  	return camelData
	  },
	  /**
	   * camel配置文件数据源beans
	   */
	  addDatasourceBeans() {
	  	if(this.dataSourcesBeans != '') return
			let datasourceTree = this.datasourceTree
			let beanStr = ''
			if(datasourceTree) {
				Object.keys(datasourceTree).forEach(function(key) {
					beanStr = os.EOL + '<bean id="datasource-' + datasourceTree[key].data.sourceName
						+ '" class="org.springframework.jdbc.datasource.DriverManagerDataSource">' + os.EOL
					  + '<property name="driverClassName" value="' + datasourceTree[key].data.driver + '" />' + os.EOL
					  + '<property name="url" value="' + datasourceTree[key].data.dbUrl + '" />' + os.EOL
					  + '<property name="username" value="' + datasourceTree[key].data.userName + '" />' + os.EOL
					  + '<property name="password" value="' + datasourceTree[key].data.password + '" />' + os.EOL
						+ '</bean>' + os.EOL
				})
			}
			this.dataSourcesBeans = beanStr
	  },
	  /**
	   * 添加activeMQ依赖beans
	   */
	  addJMSBeans(jmsData) {
	  	let connStr = '';
	  	if(this.jmsBeans == '') {
	  		if(!jmsData.anonymousConn) {
					connStr = '<property name="userName" value="' + jmsData.data.userName + '" />' + os.EOL
					+ '<property name="password" value="' + jmsData.data.password + '" />' + os.EOL
				}
				this.jmsBeans = os.EOL + '<bean id="jms" class="org.apache.camel.component.jms.JmsComponent">' + os.EOL
					+ '<property name="connectionFactory">' + os.EOL
					+ '<bean class="' + jmsData.data.factory + '">' + os.EOL
					+ '<property name="brokerURL" value="' + jmsData.data.jndiAddress + '" />' + os.EOL
					+ connStr
					+ '</bean>' + os.EOL
					+ '</property>' + os.EOL
					+ '</bean>' + os.EOL
					+ '<bean id="activemq" class="org.apache.activemq.camel.component.ActiveMQComponent">' + os.EOL
					+ '<property name="brokerURL" value="' + jmsData.data.jndiAddress + '" /> ' + os.EOL
					+ '</bean>' + os.EOL
					+ '<bean id="mqBeanServer" class="com.example.camel.test.camel.beans.MqBeanServer" />'
			}
	  },
	  /**
	   * 引入数据库组件处理beans
	   */
	  addDatabaseBeans() {
	  	if(this.databaseBeans == '') {
	  		this.databaseBeans = os.EOL + '<bean id="databaseBeanServer" class="com.example.camel.test.camel.beans.DatabaseBeanServer">' + os.EOL
					+ '</bean>'
	  	}
	  },
	  /**
		 * camel xml文件内容字符串
		 */
		xmlString(dirPath, camelData) {
			var xml = this.xmlHeaders + os.EOL + this.camelBeans
			xml += '<camelContext id="' + this.currentProcess + '" trace="false" xmlns="http://camel.apache.org/schema/spring">' + os.EOL
			xml += this.dataFormats
			xml += camelData
			xml += '</camelContext>' + os.EOL + '</beans>'
			return xml
		},
    changeColor (obj) {
      this.topTool.map(item => {
        item = 'StraightLine'
      })
    },
    menuClickFn (item) {
      let zoomRate = +item.title.replace('%', '') / 100
      this.visible = false
      let oldZoomRate = this.drawStyle.zoomRate
      this.UPDATE_DRAWSTYLE({ zoomRate, oldZoomRate })
    },
    documentClick (e) {
      this.visible = false
    }
  },
  mounted () {
 	}
}
</script>

<style lang="scss">
@import './css/flow.scss';

.flow-top-tool {
  border-bottom: 1px solid $borderColor;
  box-shadow: 0 2px 0px #bbb;
  border-top: 1px solid $borderColor;
  background: whiteSmoke;
  font-size: 12px;
  position: fixed;
  z-index: 999;
  width: 100%;
  top: 30px;
  padding-left: 5px;
  .ftt-container {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    .ftt-icon {
      pointer-events: none
    }
    .ftt-tool-item {
      display: flex;
      justify-content: center;
      align-items: center;
      cursor: pointer;
    }
    .ftt-label {
      span {
        margin-right: 5px;
        width: 35px;
        display: inline-block;
        text-align: center;
      }
    }
    .ftt-button {
      padding: 6px 8px;
      font-size: 16px;
    }
    .ftt-separator {
      width: 1px;
      height: 34px;
      pointer-events: none;
      background: $borderColor;
      margin-left: 6px;
      margin-right: 6px;
      margin-top: -2px;
    }
  }
}
</style>
