$(document).ready(function() {
    $('#table1').fixedHeaderTable({ footer: false, cloneHeadToFoot: true, altClass: 'odd', autoShow: false ,fixedColumns: 5});
    
    $('#table1').fixedHeaderTable('show', 0);
    
    $('#myTable02').fixedHeaderTable({ footer: true, altClass: 'odd' });
    
    $('#myTable05').fixedHeaderTable({ altClass: 'odd', footer: true, fixedColumns: 1 });
    
    $('#myTable03').fixedHeaderTable({ altClass: 'odd', footer: true, fixedColumns: 1 });
    
    $('#myTable04').fixedHeaderTable({ altClass: 'odd', footer: true, cloneHeadToFoot: true, fixedColumns: 3 });
});

 var TableRow = 
		  {
				selected:-1,
				selected1:-1,
				selectedrow:-1,
				selectedcolumn:-1,
				hoverColor:'#CCCCCC',
				defaultColor:'#eef2f9',
				onmouseover : function(trow,id)
				{
					if (TableRow.selected!= id)
						trow.bgColor=TableRow.hoverColor;
				},
				onmouseout : function(trow,id)
				{
					if (TableRow.selected!= id)
						trow.bgColor=TableRow.defaultColor;
				},
				onclick : function(trow)
				{	
					if (TableRow.selected1!=-1)
					{
						var r = document.getElementById(TableRow.selected1);	
						r.bgColor=TableRow.defaultColor;						
					}
						TableRow.selected1 =trow.id;
						trow.bgColor=TableRow.hoverColor;
				}
			}  