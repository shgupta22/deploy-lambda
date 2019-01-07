package example;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.*;
import com.amazonaws.util.IOUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class LambdaDeploy {

    public static void main(String[] args) throws IOException {

        /*InvokeRequest invokeRequest = new InvokeRequest()
                .withFunctionName("MethodHandlerLambda")
                .withPayload("\"Invoke Test Lambda\"");*/

        BasicAWSCredentials awsCreds = new BasicAWSCredentials("ACCESS_KEY", "SECRET_KEY");

        AWSLambda awsLambda = AWSLambdaClientBuilder.standard()
                .withRegion(Regions.US_EAST_1)
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds)).build();

       /* InvokeResult invokeResult = null;

        try {
            invokeResult = awsLambda.invoke(invokeRequest);
        }
        catch (Exception e) {
            System.out.println(e);
        }

        System.out.println(invokeResult.getStatusCode());
        System.out.println(invokeResult.getPayload());
        System.out.println(invokeResult.getLogResult());
        System.out.println(invokeResult.toString());*/

        ListFunctionsRequest request = new ListFunctionsRequest().withMaxItems(123);
        ListFunctionsResult response = awsLambda.listFunctions(request);

        System.out.println("Result : " + response.getFunctions());

        FileInputStream inputStream = new FileInputStream("/home/pc/git/lambdaexample/build/distributions/lambdaexample.zip");
        byte[] bytes = IOUtils.toByteArray(inputStream);
        ByteBuffer mBuf = ByteBuffer.wrap(bytes);


        CreateFunctionRequest createFunctionRequest = new CreateFunctionRequest().withFunctionName("LambdaFunctionFromCode").withRuntime("java8")
                //replace with the actual arn of the execution role you created
                .withRole("arn:aws:iam::660239660726:role/NAME")
                //is of the form of the name of your source file and then name of your function handler
                .withHandler("example.Hello::myHandler")
                .withCode(new FunctionCode().withZipFile(mBuf))
                /*.withCode(new FunctionCode())*/.withDescription("").withTimeout(15).withMemorySize(128).withPublish(true)
                .withVpcConfig(new VpcConfig());
        CreateFunctionResult createFunctionResult = awsLambda.createFunction(createFunctionRequest);

        System.out.println("Create Function : " + createFunctionResult.getFunctionName());
    }
}
