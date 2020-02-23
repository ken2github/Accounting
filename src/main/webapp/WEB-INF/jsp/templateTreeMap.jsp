<html>
  <head>
    <script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>
    <script type="text/javascript">
      google.charts.load('current', {'packages':['treemap']});
      google.charts.setOnLoadCallback(drawChart);
      function drawChart() {
        var data = google.visualization.arrayToDataTable([
          ['Settore', 		'Parent', 		'Taglia', 			'Tipo di spesa (color)'],
          ['Uscite',    	null,           0,                  0],
          ['SPS.CIBO',   	'SPS',        	1,                  0],
          ['SPS',    		'Global',       50,                 0],
          ['PRO',      		'Global',       20,                 0],
          ['PRO.MISSIONE', 	'PRO',          10,                 0],
          ['OGG',    		'Global',       25,                 0],
          ['OGG.PERSO',    	'OGG',          11,                 0]
        ]);

        tree = new google.visualization.TreeMap(document.getElementById('chart_div'));

        tree.draw(data, {
          minColor: '#f00',
          midColor: '#ddd',
          maxColor: '#0d0',
          headerHeight: 15,
          fontColor: 'black',
          fontSize: 22,
          maxDepth: 2,
          showScale: true
        });

      }
    </script>
  </head>
  <body>
    <div id="chart_div" style="width: 900px; height: 500px;"></div>
  </body>
</html>
