const fs = require('fs')
const os = require('os')
const path = require('path')
//const {mapMutations} = require('vuex')

const xmlHeaders = '<beans xmlns="http://www.springframework.org/schema/beans" '
	+ 'xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:cxf="http://camel.apache.org/schema/cxf" '
	+ 'xmlns:jaxrs="http://cxf.apache.org/jaxrs" ' + os.EOL
	+ 'xsi:schemaLocation="'
  + 'http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd' + os.EOL
  + 'http://camel.apache.org/schema/cxf http://camel.apache.org/schema/cxf/camel-cxf.xsd' + os.EOL
  + 'http://cxf.apache.org/jaxrs http://cxf.apache.org/schemas/jaxrs.xsd' + os.EOL
  + 'http://camel.apache.org/schema/spring http://camel.apache.org/schema/spring/camel-spring.xsd">'

var file = {
//	...mapMutations('flow', ['UPDATE_COMPONENT_DATA']),
	/**
	 * 写文件
	 * @param {Object} params
	 */
	writeFile: function(params) {
		// 创建目录
		var timestamp = (new Date()).valueOf()
		var dirPath = path.join(__dirname, '/output/' + timestamp)	// 目录路径
		fs.mkdir(dirPath, function(err) {
	   	if (err) {
	    	return console.error(err);
	   	}
	   	console.log('目录创建成功。');
		})
		params.filePath = dirPath
		// 创建process xml文件
		var processXml = this.processString(params)
		var processPath = path.join(dirPath, '/' + params.fileName)	// 流程配置文件路径
		fs.writeFile(processPath, processXml, function(err) {
	   	if (err) {
       	return console.error(err);
	   	}
	   	console.log('process xml文件创建成功！');
		})
		// 创建camel xml文件
		var camelXml = this.xmlString(params)
		var camelPath = path.join(dirPath, '/camel_' + params.fileName)	// 流程配置文件路径
		fs.writeFile(camelPath, camelXml, function(err) {
	   	if (err) {
       	return console.error(err);
	   	}
	   	console.log('camel xml文件创建成功！');
		})
		// 创建组件内容文件
		let componentData = JSON.parse(params.componentData)
		Object.keys(componentData).forEach(function(key) {
			var component = componentData[key]
			var componentPath = path.join(dirPath, '/' + key + '.txt')		// 组件文件路径
			fs.writeFile(componentPath, JSON.stringify(component), function(err) {
		   	if (err) {
		      return console.error(err);
		   	}
		  	console.log('组件文件创建成功！');
			})
		})
		return params.filePath
	},
	/**
	 * 同步读文件
	 * @param {Object} path
	 */
	readFileSync: function(path) {
		// 同步读取
		var data = fs.readFileSync(path)
		return data
	},
	/**
	 * 异步读取文件
	 * @param {Object} path
	 */
	readFile: function(params) {
//		var that = this
		// 异步读取
//		var id = params.id
		fs.readFile(params.path, function (err, data) {
	   	if (err) {
       	return console.error(err);
	   	}
	   	console.log('异步读取: ' + data);
//    that.UPDATE_COMPONENT_DATA({
//    	[id]: data
//    })
		})
		return 'success'
	},
	/**
	 * 流程xml文件内容字符串
	 * @param {Object} params
	 */
	xmlString: function(params) {
//		console.log(params)
		var xml = xmlHeaders + os.EOL
		let componentData = JSON.parse(params.componentData)
		Object.keys(componentData).forEach(function(key) {
			xml += '<bean id="' + key + '" class="'+ params.filePath + '/' + key +'.txt"></bean>' + os.EOL
		})
		let fileName = params.fileName.substring(0, params.fileName.indexOf('.'))
		xml += '<camelContext id="' + fileName + '" trace="false" xmlns="http://camel.apache.org/schema/spring">' + os.EOL
		xml += '<route autoStartup="true">' + os.EOL
		xml += '</route>' + os.EOL + '</camelContext>' + os.EOL + '</beans>'
		return xml
	},
	/**
	 * 流程节点关系字符串
	 * @param {Object} params
	 */
	processString: function(params) {
  	var process = '<process>'
  	let nodeData = JSON.parse(params.nodeData)
  	let componentXml = ''
  	let nodeXml = ''
		Object.keys(nodeData).forEach(function(key) {
			var node = nodeData[key]
			nodeXml += '<node id="' + key + '" type="' + node.type + '">' + os.EOL
			nodeXml += JSON.stringify(node) + os.EOL
			nodeXml += '</node>' + os.EOL

			componentXml += '<componentData id="' + key + '" class="'+ params.filePath + '/' + key +'.txt"></componentData>' + os.EOL
		})
		let lineData = JSON.parse(params.lineData)
		let lineXml = ''
		Object.keys(lineData).forEach(function(key) {
			var line = lineData[key]
			lineXml += '<line id="' + key + '" type="' + line.type + '">' + os.EOL
			lineXml += JSON.stringify(line) + os.EOL
			lineXml += '</line>' + os.EOL
		})
		process = process + os.EOL + componentXml + nodeXml + lineXml + '</process>' + os.EOL
		return process
  }
}

module.exports = file
