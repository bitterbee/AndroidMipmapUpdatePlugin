package operation;

import com.netease.tools.model.ImgStatus;
import com.netease.tools.util.Fio;

import java.io.File;
import java.io.IOException;

/**
 * Created by zyl06 on 2018/7/20.
 */
public class ImgOperation {

    private String inPath;
    private String toPath;
    private ImgStatus status;

    private String toFileName;

    public ImgOperation(String inPath, String toPath) {
        this.inPath = inPath;
        this.toPath = toPath;

        File inFile = new File(inPath);
        File toFile = new File(toPath);
        if (inFile.exists()) {
            if (compare(inFile, toFile)) {
                status = ImgStatus.NO_MODIFY;
            } else if (!toFile.exists()) {
                status = ImgStatus.ADD;
            } else {
                status = ImgStatus.MODIFY;
            }
        } else if (toFile.exists()) {
            status = ImgStatus.DELETE;
        } else {
            status = ImgStatus.NO_MODIFY;
        }

        toFileName = toFile.getName();
    }

    public String inPath() {
        return inPath;
    }

    public String toPath() {
        return toPath;
    }

    public ImgStatus status() {
        return status;
    }

    public String run() throws Exception {
        switch (status) {
            case ADD:
            case MODIFY:
                return addOrModify();
            case DELETE:
                return delete();
        }

        return "";
    }

    private String addOrModify() throws Exception {
        File toFile = new File(toPath);
        boolean toFileExist = toFile.exists();
        if (toFileExist) {
            if (!toFile.delete()) {
                String errMsg = "delete " + toPath + " failed";
                throw new Exception(errMsg);
            }
        }

        String operation = toFileExist ? "replace " : "add ";
        try {
            boolean copySuccess = Fio.copy(inPath, toPath);
            if (!copySuccess) {
                String errMsg = operation + inPath + " to " + toPath + " failed" + "\n";
                throw new Exception(errMsg);
            }

            return operation + inPath + " to " + toPath + "\n";
        } catch (IOException e) {
            e.printStackTrace();
            String errMsg = operation + inPath + " to " + toPath + " failed: error is" + e.toString() + "\n";
            throw new Exception(errMsg);
        }
    }

    private String delete() throws Exception {
        File file = new File(toPath);
        if (!file.delete()) {
            String errMsg = "delete " + file.getAbsolutePath() + " failed";
            throw new Exception(errMsg);
        }
        return "delete " + toPath;
    }

    @Override
    public String toString() {
        switch (status) {
            case ADD:
                return "add " + inPath + " to " + toPath;
            case MODIFY:
                return "replace " + inPath + " to " + toPath;
            case DELETE:
                return "delete " + toPath;
            case NO_MODIFY:
                return "no modify " + inPath + " to " + toPath;
        }
        return "unknown operation " + inPath + " to " + toPath;
    }

    public String toSimpleString() {
        switch (status) {
            case ADD:
                return "A-" + toFileName;
            case MODIFY:
                return "M-" + toFileName;
            case DELETE:
                return "D-" + toFileName;
            case NO_MODIFY:
                return "no modify-" + toFileName;
        }
        return "unknown operation-" + toFileName;
    }

    private boolean compare(File inFile, File outFile) {
        if (!inFile.exists()) {
            return false;
        }
        if (!outFile.exists()) {
            return false;
        }

        String inMd5 = Fio.calcMd5(inFile);
        String outMd5 = Fio.calcMd5(outFile);

        return inMd5 != null && inMd5.equals(outMd5);
    }
}
