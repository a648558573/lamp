$(function () {
$('#container').highcharts({
 chart: {type: 'line'},
  title: {text: '(399-测试)终端按月能耗统计'},
  colors:['#ffd700','#254117','#600017','#500FF7','#993117','Gray'],
   xAxis: {categories: []},
   yAxis: {title: {text: '399-测试耗电量（千瓦时kwh）'}},
    plotOptions: {column: {dataLabels: {enabled: true , rotation: -90,color: 'black', align: 'left'}},line: {dataLabels: {enabled: true , rotation: -90,color: 'black', align: 'left'}} },
     exporting:{ enabled:true },
      credits: { enabled: false },
       series: [{upColor: Highcharts.getOptions().colors[3],name: '总有功耗电量(kw-h)',visible:true,data: []}, {name: '总无功耗电量(kvarh)',visible:false,data: []}, {name: '终端数',visible:false,data: []}, {name: '灯数',visible:false,data: []}, {name: '照明密度',visible:false,data: []}, {name: '照明密度标准值',visible:false,data: []}]
       });
        });