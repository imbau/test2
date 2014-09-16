<?php  
require_once('pdf/tcpdf.php');  
  
// create new PDF document  
$pdf = new TCPDF(PDF_PAGE_ORIENTATION, PDF_UNIT, PDF_PAGE_FORMAT, true, 'UTF-8', false);  
  
//set image scale factor  
$pdf->setImageScale(PDF_IMAGE_SCALE_RATIO);  
  
// add a page  
$pdf->AddPage();  
  
// create some HTML content  
$html = file_get_contents('pdf_i.php');  
  
// set core font  
$pdf->SetFont('msungstdlight', '', 10);  
  
// output the HTML content  
$pdf->writeHTML($html, true, 0, true, true);  
  
// reset pointer to the last page  
$pdf->lastPage();  
  
//Close and output PDF document  
$pdf->Output('test.pdf', 'I');  
?>  