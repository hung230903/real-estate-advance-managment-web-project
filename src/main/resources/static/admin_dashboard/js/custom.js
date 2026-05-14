/**
 * Tự động highlight các trường input đã có dữ liệu
 */
function initHighlightFilledFields() {
    // 1. Highlight khi load trang
    $('input.form-control, select.form-control, textarea.form-control').each(function () {
        const type = $(this).attr('type');
        const val = $(this).val();
        
        // Tránh highlight file, password và checkbox/radio (vì checkbox có style riêng)
        if (val && val.toString().trim() !== "" && type !== 'file' && type !== 'password') {
            $(this).addClass('highlight-filled');
        }
    });

    // 2. Lắng nghe sự kiện thay đổi để cập nhật highlight
    $(document).on('input change', 'input.form-control, select.form-control, textarea.form-control', function() {
        const type = $(this).attr('type');
        const val = $(this).val();
        
        if (val && val.toString().trim() !== "" && type !== 'password') {
            $(this).addClass('highlight-filled');
        } else {
            $(this).removeClass('highlight-filled');
        }
    });
}

$(document).ready(function() {
    initHighlightFilledFields();
});
