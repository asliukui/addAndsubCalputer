package calputer.demo.com.calputer.widget;

import android.app.Activity;
import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.text.Editable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import java.math.BigDecimal;
import java.util.List;

import calputer.demo.com.calputer.R;


/**
 * Author: liukui
 * Date:   2018/3/13
 * Description: 键盘自定义,只包含加减运算，输入限制：整数部分最大8位，小数部分最多2位。
 */
public class KeyboardUtil {
    private KeyboardView keyboardView;   //keyBoardView组件
    private Keyboard k1;// 数字键盘
    private EditText ed;
    private int KEY_ENTER_CODE = 13;
    private Context context;

    public KeyboardUtil(Activity act, Context ctx, EditText edit) {
        context = ctx;
        this.ed = edit;
        k1 = new Keyboard(ctx, R.xml.number);
        keyboardView = (KeyboardView) act.findViewById(R.id.keyboard_view);
        keyboardView.setKeyboard(k1);
        keyboardView.setEnabled(true);
        keyboardView.setPreviewEnabled(false);
        keyboardView.setOnKeyboardActionListener(listener);
        ed.setOnTouchListener(viewTouch);
    }


    private KeyboardView.OnKeyboardActionListener listener = new KeyboardView.OnKeyboardActionListener() {
        @Override
        public void swipeUp() {
            // TODO Auto-generated method stub
        }


        @Override
        public void swipeRight() {
            // TODO Auto-generated method stub
        }


        @Override
        public void swipeLeft() {
            // TODO Auto-generated method stub
        }


        @Override
        public void swipeDown() {
            // TODO Auto-generated method stub
        }


        @Override
        public void onText(CharSequence text) {
            // TODO Auto-generated method stub
        }


        @Override
        public void onRelease(int primaryCode) {
            if (KEY_ENTER_CODE == primaryCode) {
                if (null != onEnterListener) {
                    onEnterListener.enter();
                }
                hideKeyboard();
            }
        }

        @Override
        public void onPress(int primaryCode) {
        }

        @Override
        public void onKey(int primaryCode, int[] keyCodes) {
            Editable editable = ed.getText();
            int start = ed.getSelectionStart();
            if (primaryCode == Keyboard.KEYCODE_CANCEL) { // 完成
                hideKeyboard();
            } else if (primaryCode == Keyboard.KEYCODE_DELETE) {
                if (editable != null && editable.length() > 0) {
                    if (start > 0) {
                        editable.delete(start - 1, start);
                    }
                }
            } else if (primaryCode == 57419) { // go left
                if (start > 0) {
                    ed.setSelection(start - 1);
                }
            } else if (primaryCode == 57421) { // go right
                if (start < ed.length()) {
                    ed.setSelection(start + 1);
                }
            } else if (primaryCode == KeyEvent.KEYCODE_NUMPAD_EQUALS) {// =
                String editerValue = ed.getText().toString();
                editable.clear();
                editable.append(splitString(editerValue));
                changeDefault();
                keyboardView.setKeyboard(k1);

            } else if (primaryCode == KeyEvent.KEYCODE_PLUS) {// +
                String editerValue = ed.getText().toString();
                if (TextUtils.isEmpty(editerValue)) {
                    return;
                } else if (editerValue.endsWith("+")) {
                    return;
                } else if (editerValue.endsWith(".")) {
                    editable.delete(editerValue.length() - 1, editerValue.length());
                    editable.append("+");
                } else if (editerValue.contains("-") || editerValue.contains("+")) {
                    editable.clear();
                    editable.append(splitString(editerValue)).append("+");
                } else {
                    editable.insert(start, "+");
                }
                changeKey();
                keyboardView.setKeyboard(k1);
            } else if (primaryCode == KeyEvent.KEYCODE_MINUS) {// -
                String editerValue = ed.getText().toString();
                if (TextUtils.isEmpty(editerValue)) {
                    return;
                } else if (editerValue.endsWith("-")) {
                    return;
                } else if (editerValue.endsWith(".")) {
                    editable.delete(editerValue.length() - 1, editerValue.length());
                    editable.append("-");
                } else if (editerValue.contains("-") || editerValue.contains("+")) {
                    editable.clear();
                    editable.append(splitString(editerValue)).append("-");
                } else {
                    editable.insert(start, "-");
                }
                changeKey();
                keyboardView.setKeyboard(k1);
            } else if (primaryCode == 46) { //num_dot
                String editerValue = ed.getText().toString();
                if (TextUtils.isEmpty(editerValue)) {
                    editable.insert(start, "0.");
                } else if (editerValue.endsWith("+") || editerValue.endsWith("-")) {
                    editable.insert(start, "0.");
                } else if (editerValue.startsWith("-") && editerValue.substring(1).contains("-")) {//当被减数为负数时，减数限制
                    String substring = editerValue.substring(1);
                    setSecondPortForDoc(editable, start, substring, "-");
                } else if (!editerValue.contains("-") && !editerValue.contains("+") && editerValue.contains(".")) {//无运算符号时，限制"."唯一
                    return;
                } else if (editerValue.contains("-")) {//当被减数为正数时，减数限制
                    setSecondPortForDoc(editable, start, editerValue, "-");
                } else if (editerValue.contains("+")) {//加法时，加数限制
                    setSecondPortForDoc(editable, start, editerValue, "\\+");
                } else {
                    editable.insert(start, ".");
                }
            } else if (primaryCode == 107) {
                changeKey();
                keyboardView.setKeyboard(k1);
            } else {
                String editerValue = ed.getText().toString();
                if (!editerValue.startsWith("-")) {//正数
                    setNum((char) primaryCode, editable, start, editerValue);
                } else if (editerValue.startsWith("-")) {//负数
                    editerValue = editerValue.substring(1);
                    setNum((char) primaryCode, editable, start, editerValue);
                }
            }
        }
    };

    private void setNum(char primaryCode, Editable editable, int start, String editerValue) {
        if (editerValue.contains("+")) {//加法
            String[] strings = editerValue.split("\\+");
            if (strings.length == 2) {
                editerValue = strings[1];
                setSingleNum(primaryCode, editable, start, editerValue);
            } else {
                setSingleNum(primaryCode, editable, start, editerValue);
            }
        } else if (editerValue.contains("-")) {//减法
            String[] strings = editerValue.split("-");
            if (strings.length == 2) {
                editerValue = strings[1];
                setSingleNum(primaryCode, editable, start, editerValue);
            } else {
                setSingleNum(primaryCode, editable, start, editerValue);
            }
        } else {
            setSingleNum(primaryCode, editable, start, editerValue);
        }
    }

    //设置数字长度限制
    private void setSingleNum(char primaryCode, Editable editable, int start, String editerValue) {
        if (editerValue.length() < 9 && !editerValue.contains(".")
                || (editerValue.contains("+") || editerValue.contains("-"))) {//正整数
            editable.insert(start, Character.toString(primaryCode));
        } else if ((editerValue.length() < 12 && editerValue.contains(".") && editerValue.indexOf(".") > editerValue.length() - 3)
                || (editerValue.length() <= 12 && !editerValue.contains(".") && (editerValue.contains("+") || editerValue.contains("-")))) {//正数-小数
            editable.insert(start, Character.toString(primaryCode));
        }
    }

    /**
     * 设置运算符后，第二部分小数点唯一。
     *
     * @param editable
     * @param start
     * @param string
     * @param splitStr
     */
    private void setSecondPortForDoc(Editable editable, int start, String string, String splitStr) {
        String[] strings = string.split(splitStr);
        if (strings.length == 2 && !strings[1].contains(".")) {
            editable.insert(start, ".");
        }
    }

    private void changeDefault() {
        List<Keyboard.Key> keylist = k1.getKeys();
        for (Keyboard.Key key : keylist) {
            if (key.codes[0] == KeyEvent.KEYCODE_NUMPAD_EQUALS) {
                key.icon = context.getResources().getDrawable(R.drawable.keyboard_selector);
                key.codes[0] = 13;
                key.label = null;
                break;
            }
        }
    }

    /**
     * 键盘大小写切换
     */
    private void changeKey() {
        List<Keyboard.Key> keylist = k1.getKeys();
        for (Keyboard.Key key : keylist) {
            if (key.label == null) {
                if (key.codes[0] == 13) {
                    key.codes[0] = KeyEvent.KEYCODE_NUMPAD_EQUALS;
                    key.label = "=";
                }
                continue;
            }
            key.label = key.label.toString();
            key.codes[0] = key.codes[0];

        }
    }


    public void showKeyboard() {
        int visibility = keyboardView.getVisibility();
        if (visibility == View.GONE || visibility == View.INVISIBLE) {
            keyboardView.setVisibility(View.VISIBLE);
        }
    }

    public void hideKeyboard() {
        int visibility = keyboardView.getVisibility();
        if (visibility == View.VISIBLE) {
            keyboardView.setVisibility(View.INVISIBLE);
        }
    }

    public void setOnEnterListener(EnterListener onEnterListener) {
        this.onEnterListener = onEnterListener;
    }

    private EnterListener onEnterListener;

    public interface EnterListener {
        void enter();
    }

    private String splitString(String string) {
        String sign = "";
        String[] num = {"0", "0"};
        String[] split;
        if (string.startsWith("+") || string.startsWith("-")) {
            sign = string.substring(0, 1);
            string = string.substring(1);
        }
        if (string.contains("+")) {
            split = string.split("\\+");
            if (split.length == 1) {
                num[0] = sign + split[0];
            } else {
                num = split;
                num[0] = sign + split[0];
            }
            return add(num[0], num[1]);

        } else if (string.contains("-")) {
            split = string.split("-");
            if (split.length == 1) {
                num[0] = sign + split[0];
            } else {
                num = split;
                num[0] = sign + split[0];
            }
            return sub(num[0], num[1]);
        } else {
            return sign + string;
        }
    }

    /**
     * 提供精确的加法运算。
     *
     * @param v1 被加数
     * @param v2 加数
     * @return 两个参数的和
     */
    public String add(String v1, String v2) {
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return b1.add(b2).toString();
    }

    /**
     * 提供精确的减法运算。
     *
     * @param v1 被减数
     * @param v2 减数
     * @return 两个参数的差
     */
    public String sub(String v1, String v2) {
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return b1.subtract(b2).toString();
    }

    View.OnTouchListener viewTouch = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            KeyboardUtil.this.showKeyboard();
            return true;
        }
    };

}



