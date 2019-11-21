package restapi.bankfileconverter.service;

import restapi.bankfileconverter.api.InpuImplicitFileInfo;
import restapi.bankfileconverter.api.InputBase64FileInfo;
import restapi.bankfileconverter.api.InputImplicitBase64FileInfo;
import restapi.bankfileconverter.api.OutputFileInfo;

public interface BankFileReaderService {

	OutputFileInfo convertToTransactions(InpuImplicitFileInfo iifi);

	OutputFileInfo convertToTransactions(InputBase64FileInfo ifi);

	OutputFileInfo convertToTransactions(InputImplicitBase64FileInfo iifi);
}
