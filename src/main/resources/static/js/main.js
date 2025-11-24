(function ($) {
    var search_result = new Vue({
        el: '#search-result',
        data: {
            search_result: {}
        },
        methods: {
            addWish: function () {
                var payload = this.search_result;
                if (!payload || !payload.title) {
                    console.warn("저장할 검색 결과가 없습니다.");
                    return;
                }
                $.ajax({
                    type: "POST",
                    url: "/taste-map",
                    contentType: "application/json",
                    data: JSON.stringify(payload),
                    success: function () {
                        getWishList();
                    },
                    error: function (xhr, status, err) {
                        console.error("저장 실패", status, err);
                    }
                });
            }
        }
    });

    var wish_list = new Vue({
        el: '#wish-list',
        data: {
            wish_list: []
        },
        methods: {
            deleteWish: function (id) {
                if (!confirm("정말 삭제하시겠습니까?")) return;
                $.ajax({
                    type: "DELETE",
                    url: `/taste-map/${id}`,
                    success: function () {
                        getWishList();
                    },
                    error: function (xhr, status, err) {
                        console.error("삭제 실패", status, err);
                    }
                });
            }
        }
    });

    function doSearch(query) {
        if (!query) return;
        $.get(`/taste-map/search`, { query: query })
            .done(function (response) {
                search_result.search_result = response || {};
                $('#search-result').show();
            })
            .fail(function (xhr, status, err) {
                console.error("검색 실패", status, err);
                search_result.search_result = {};
                $('#search-result').hide();
            });
    }

    function getWishList() {
        $.get(`/taste-map/find-all`)
            .done(function (response) {
                wish_list.wish_list = response || [];
            })
            .fail(function (xhr, status, err) {
                console.error("위시리스트 조회 실패", status, err);
                wish_list.wish_list = [];
            });
    }

    $("#searchButton").click(function () {
        const query = $("#searchBox").val();
        doSearch(query);
    });

    $("#searchBox").keydown(function (key) {
        if (key.key === 'Enter' || key.keyCode === 13) {
            const query = $("#searchBox").val();
            doSearch(query);
        }
    });

    $("#wishButton").click(function () {
        search_result.addWish();
    });

    $(document).ready(function () {
        getWishList();
        $('#search-result').hide();
    });

})(jQuery);
