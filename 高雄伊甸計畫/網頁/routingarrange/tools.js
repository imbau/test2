/* jQuery */
/*!	
* 
*
* Copyright 2013
* 
*
*
* 
*
* Date: 
*/
/**************************************************/

/*******************�r��ʵe*************************/ 
$(function() {
					$("#letter-container h2 a").lettering();
				});
		$(function(){
		// �� #menu li �[�W hover �ƥ�
		$('#menu>li').hover(function(){
			// ����� li �����l���
			var _this = $(this),
				_subnav = _this.children('ul');			
			// �ܧ�ثe���ﶵ���I���C��
			// �P����ܤl���(�p�G������)
			_this.css('backgroundColor', '#06c').siblings().css('backgroundColor', '');
			_subnav.css('display', 'block');
		} , function(){
			// �P�����äl���(�p�G������)
			// �]�i�H���y��W�����g�k
			$(this).children('ul').css('display', 'none');
		});
		
		// �����W�s������u��
		$('a').focus(function(){
			this.blur();
		});
	});   

	
/****************�r��ʵe************************/


/*******************progressbar*********************/
  function test(progres) {
                $( "#progressbar" ).progressbar({
                        value:progres
                });
        };
/*******************progressbar*********************/
